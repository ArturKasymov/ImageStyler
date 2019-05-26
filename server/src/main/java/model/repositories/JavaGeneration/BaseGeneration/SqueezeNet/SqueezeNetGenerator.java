package model.repositories.JavaGeneration.BaseGeneration.SqueezeNet;

import model.repositories.JavaGeneration.core.BaseGenerationRepo;
import model.repositories.JavaGeneration.core.GenerationException;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.AbstractLayer;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.SqueezeNet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.executioner.OpExecutioner;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.AdamUpdater;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SqueezeNetGenerator extends BaseGenerationRepo {

    public SqueezeNetGenerator(BufferedImage contentImage, BufferedImage styleImage) {
        super(contentImage, styleImage, 0.0);
        Nd4j.getExecutioner().setProfilingMode(OpExecutioner.ProfilingMode.DISABLED);
    }

    @Override
    public BufferedImage generate() throws GenerationException {
        try {
            ComputationGraph Graph = loadModel(false);
            INDArray generatedImage = initGeneratedImage();
            Map<String, INDArray> contentActivation = Graph.feedForward(contentImage, true);
            Map<String, INDArray> styleActivation = Graph.feedForward(styleImage, true);
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
                Map<String, INDArray> forwardActivation = Graph.feedForward(new INDArray[] { generatedImage }, true, false);
                HashMap<String, INDArray> dropoutMasks = saveDropoutMasks(Graph);
                INDArray styleGrad = backPropStyles(Graph, styleActivationGram, forwardActivation, dropoutMasks);
                INDArray contentGrad = backPropContent(Graph, contentActivation, forwardActivation, dropoutMasks);
                INDArray totalGrad = contentGrad.muli(ALPHA).addi(styleGrad.muli(BETA));
                if (i % 5 == 0) {
                    double totalLoss = contentLoss(ALPHA, contentActivation.get(CONTENT_LAYER_NAME), forwardActivation.get(CONTENT_LAYER_NAME)) +
                            styleLoss(styleActivationGram, forwardActivation);
                    System.out.println("Loss: " + totalLoss);
                    INDArray gradients = contentGrad.dup();
                    BufferedImage output = fromMatrix(gradients);
                    URL resource = getClass().getResource(".");
                    File file = new File(resource.getPath() + "/iteration" + i + ".png");
                    ImageIO.write(output, "png", file);
                }
                optim.applyUpdater(totalGrad, i, 0);
                generatedImage.subi(totalGrad);
            }

            return fromMatrix(generatedImage);

            //return fromMatrix(mirrored(contentImage));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenerationException();
        }
    }

    @Override
    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = SqueezeNet.builder().build();
        ComputationGraph squeezeNet = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        squeezeNet.initGradientsView();
        return squeezeNet;
    }

    @Override
    protected void initHyperParams(double d) {
        IMAGE_PREPROCESSOR = SqueezeNetHyperParameters.IMAGE_PREPROCESSOR;
        HEIGHT = SqueezeNetHyperParameters.HEIGHT;
        WIDTH = SqueezeNetHyperParameters.WIDTH;

        ALL_LAYERS = SqueezeNetHyperParameters.ALL_LAYERS;
        STYLE_LAYERS = SqueezeNetHyperParameters.STYLE_LAYERS;
        CONTENT_LAYER_NAME = SqueezeNetHyperParameters.CONTENT_LAYER_NAME;

        ITERATIONS = SqueezeNetHyperParameters.ITERATIONS;
        ALPHA = SqueezeNetHyperParameters.ALPHA;
        BETA = SqueezeNetHyperParameters.BETA;

        LEARNING_RATE = SqueezeNetHyperParameters.LEARNING_RATE;
        BETA1 = SqueezeNetHyperParameters.BETA1;
        BETA2 = SqueezeNetHyperParameters.BETA2;
        EPSILON = SqueezeNetHyperParameters.EPSILON;
        NOISE = SqueezeNetHyperParameters.NOISE;
    }

    @Override
    protected INDArray backPropagate(ComputationGraph graph, INDArray dLdA, int startIndex, HashMap<String, INDArray> masks) {
        for (int i = startIndex; i > 0; i--) {
            Layer layer = graph.getLayer(ALL_LAYERS[i]);
            if (layer!=null && (layer.type()==Layer.Type.CONVOLUTIONAL||layer.type()==Layer.Type.SUBSAMPLING)) {
                ((Dropout) ((AbstractLayer) layer).layerConf().getIDropout()).setMask(masks.get(ALL_LAYERS[i]));
            }
        }
        for (int i = startIndex; i > 0; i--) {
            Layer layer = graph.getLayer(ALL_LAYERS[i]);
            //System.out.println(ALL_LAYERS[i]);
            if (layer==null) {
                INDArray dLdADownstream = dLdA.get(NDArrayIndex.all(), NDArrayIndex.interval(0, (int)dLdA.shape()[1]/2), NDArrayIndex.all(), NDArrayIndex.all());
                INDArray dLdAUpstream = dLdA.get(NDArrayIndex.all(), NDArrayIndex.interval((int)dLdA.shape()[1]/2, dLdA.shape()[1]), NDArrayIndex.all(), NDArrayIndex.all());
                i--;
                Layer Uplayer = graph.getLayer(ALL_LAYERS[i]);
                dLdAUpstream = Uplayer.backpropGradient(dLdAUpstream, LayerWorkspaceMgr.noWorkspaces()).getSecond();
                i--;
                Layer Downlayer = graph.getLayer(ALL_LAYERS[i]);
                dLdADownstream = Downlayer.backpropGradient(dLdADownstream, LayerWorkspaceMgr.noWorkspaces()).getSecond();
                i--;
                Uplayer = graph.getLayer(ALL_LAYERS[i]);
                dLdAUpstream = Uplayer.backpropGradient(dLdAUpstream, LayerWorkspaceMgr.noWorkspaces()).getSecond();
                i--;
                Downlayer = graph.getLayer(ALL_LAYERS[i]);
                dLdADownstream = Downlayer.backpropGradient(dLdADownstream, LayerWorkspaceMgr.noWorkspaces()).getSecond();
                dLdA = dLdADownstream.addi(dLdAUpstream);
                i--;
                layer = graph.getLayer(ALL_LAYERS[i]);
            }
            dLdA = layer.backpropGradient(dLdA, LayerWorkspaceMgr.noWorkspaces()).getSecond();
        }
        return dLdA;
    }
}
