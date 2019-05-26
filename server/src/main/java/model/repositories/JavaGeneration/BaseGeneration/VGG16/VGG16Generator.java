package model.repositories.JavaGeneration.BaseGeneration.VGG16;

import model.repositories.JavaGeneration.core.BaseGenerationRepo;
import model.repositories.JavaGeneration.core.GenerationException;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.AdamUpdater;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VGG16Generator extends BaseGenerationRepo {

    public VGG16Generator(BufferedImage contentImage, BufferedImage styleImage, double d) {
        super(contentImage, styleImage, d);
    }

    @Override
    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = VGG16.builder().build();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        vgg16.initGradientsView();
        return vgg16;
    }

    @Override
    public BufferedImage generate() throws GenerationException {
        try {
            System.out.println("Content shape - (" + contentImage.shape()[2] + ", " + contentImage.shape()[3] + ")");
            System.out.println("Style shape - (" + styleImage.shape()[2] + ", " + styleImage.shape()[3] + ")");
            int tillIndexContent = layerIndex(CONTENT_LAYER_NAME);
            int tillIndexStyle = layerIndex(STYLE_LAYERS[STYLE_LAYERS.length-1].split(",")[0]);
            int tillIndex = Math.max(tillIndexContent, tillIndexStyle);
            ComputationGraph Graph = loadModel(false);
            INDArray generatedImage = initGeneratedImage();
            Map<String, INDArray> contentActivation = Graph.feedForward(contentImage, tillIndex, true);
            /*for (String s : ALL_LAYERS) {
                System.out.println(s);
                System.out.println(Arrays.toString(contentActivation.get(s).shape()));
            }*/
            Map<String, INDArray> styleActivation = Graph.feedForward(styleImage, tillIndex, true);
            HashMap<String, INDArray> styleActivationGram = initStyleGramMap(styleActivation);
            AdamUpdater optim = createAdamUpdater();
            for (int i = 0; i < ITERATIONS; i++) {
                if (i % 5 == 0) {
                    /*if ( i%25 == 0) {
                        INDArray genImage = generatedImage.dup();
                        BufferedImage output = SwingFXUtils.fromFXImage(fromMatrix(genImage), null);
                        URL resource = getClass().getResource(".");
                        File file = new File(resource.getPath() + "/iteration" + i + ".png");
                        ImageIO.write(output, "png", file);
                    }*/
                }
                Map<String, INDArray> forwardActivation = Graph.feedForward(new INDArray[] { generatedImage }, tillIndex, true, false);
                INDArray styleGrad = backPropStyles(Graph, styleActivationGram, forwardActivation);
                INDArray contentGrad = backPropContent(Graph, contentActivation, forwardActivation);
                INDArray totalGrad = contentGrad.muli(ALPHA).addi(styleGrad.muli(BETA));
                // SAVE RADIENTS TO FILE AND COMPARE WITH SQUEEZENET'S ONES
                if (i % 5 == 0) {
                    /*double totalLoss = contentLoss(ALPHA, contentActivation.get(CONTENT_LAYER_NAME), forwardActivation.get(CONTENT_LAYER_NAME)) +
                            styleLoss(styleActivationGram, forwardActivation);
                    System.out.println("Loss: " + totalLoss);*/
                    INDArray gradients = contentGrad.dup();
                    BufferedImage output = fromMatrix(gradients);
                    URL resource = getClass().getResource(".");
                    File file = new File(resource.getPath() + "/iteration" + i + ".png");
                    ImageIO.write(output, "png", file);
                }
                optim.applyUpdater(totalGrad, i, 0);
                generatedImage.subi(totalGrad);
            }
            System.out.println("Result shape - (" + generatedImage.shape()[2] + ", " + generatedImage.shape()[3] + ")");
            return fromMatrix(generatedImage);

            //return fromMatrix(mirrored(contentImage));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenerationException();
        }
    }

    @Override
    protected void initHyperParams(double d) {
        IMAGE_PREPROCESSOR = VGG16HyperParameters.IMAGE_PREPROCESSOR;
        HEIGHT = VGG16HyperParameters.HEIGHT;
        WIDTH = VGG16HyperParameters.WIDTH;

        ALL_LAYERS = VGG16HyperParameters.ALL_LAYERS;
        STYLE_LAYERS = VGG16HyperParameters.STYLE_LAYERS;
        CONTENT_LAYER_NAME = VGG16HyperParameters.CONTENT_LAYER_NAME;

        ITERATIONS = VGG16HyperParameters.ITERATIONS;
        ALPHA = VGG16HyperParameters.ALPHA;
        BETA = VGG16HyperParameters.BETA;

        LEARNING_RATE = VGG16HyperParameters.LEARNING_RATE;
        BETA1 = VGG16HyperParameters.BETA1;
        BETA2 = VGG16HyperParameters.BETA2;
        EPSILON = VGG16HyperParameters.EPSILON;
        NOISE = VGG16HyperParameters.NOISE;

        W = d;
    }

    protected INDArray backPropStyles(ComputationGraph graph, HashMap<String, INDArray> gramActivations, Map<String, INDArray> forwardActivations) {
        INDArray backProp = Nd4j.zeros(1, CHANNELS, HEIGHT, WIDTH);
        for (String s : STYLE_LAYERS) {
            String[] spl = s.split(",");
            String layerName = spl[0];
            double weight = Double.parseDouble(spl[1]) * W;
            INDArray gramActivation = gramActivations.get(layerName);
            INDArray forwardActivation = forwardActivations.get(layerName);
            int index = layerIndex(layerName);
            INDArray derivativeStyle = derivStyleLossInLayer(gramActivation, forwardActivation).transpose();
            backProp.addi(backPropagate(graph, derivativeStyle.reshape(forwardActivation.shape()), index).muli(weight));
        }
        return backProp;
    }

    protected INDArray backPropContent(ComputationGraph graph, Map<String, INDArray> contentActivations, Map<String, INDArray> forwardActivations) {
        INDArray contentActivation = contentActivations.get(CONTENT_LAYER_NAME);
        INDArray forwardActivation = forwardActivations.get(CONTENT_LAYER_NAME);
        INDArray derivativeContent = derivContentLossInLayer(contentActivation, forwardActivation);
        return backPropagate(graph, derivativeContent.reshape(forwardActivation.shape()), layerIndex(CONTENT_LAYER_NAME));
    }

    protected INDArray backPropagate(ComputationGraph graph, INDArray dLdA, int startIndex) {
        for (int i = startIndex; i > 0; i--) {
            Layer layer = graph.getLayer(ALL_LAYERS[i]);
            dLdA = layer.backpropGradient(dLdA, LayerWorkspaceMgr.noWorkspaces()).getSecond();
        }
        return dLdA;
    }
}
