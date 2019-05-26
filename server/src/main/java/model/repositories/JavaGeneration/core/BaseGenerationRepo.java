package model.repositories.JavaGeneration.core;

import net.coobird.thumbnailator.Thumbnails;
import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.AbstractLayer;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.indexing.functions.Value;
import org.nd4j.linalg.learning.AdamUpdater;
import org.nd4j.linalg.learning.config.Adam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseGenerationRepo implements Generator {

    protected INDArray contentImage;
    protected INDArray styleImage;

    protected int INITIAL_HEIGHT;
    protected int INITIAL_WIDTH;

    protected static int HEIGHT;
    protected static int WIDTH;
    protected final static int CHANNELS = 3;

    protected static double LEARNING_RATE;
    protected static double BETA1;
    protected static double BETA2;
    protected static double EPSILON;
    protected static double NOISE;

    protected static int ITERATIONS;

    protected static String[] ALL_LAYERS;
    protected static String[] STYLE_LAYERS;
    protected static String CONTENT_LAYER_NAME;

    protected static double ALPHA;
    protected static double BETA;

    protected DataNormalization IMAGE_PREPROCESSOR;
    protected final ImageLoader LOADER = new ImageLoader(HEIGHT, WIDTH, CHANNELS);

    public BaseGenerationRepo(BufferedImage contentImage, BufferedImage styleImage) {
        initHyperParams();
        INITIAL_HEIGHT = (int) contentImage.getHeight();
        INITIAL_WIDTH = (int) contentImage.getWidth();
        try {
            this.contentImage = toMatrix(contentImage);
            this.styleImage = toMatrix(styleImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initHyperParams() {

    }

    protected INDArray mirrored(INDArray image) {
        return image.permute(0, 1, 3, 2);
    }

    public BufferedImage generate() throws GenerationException {
        try {
            /*int tillIndexContent = layerIndex(CONTENT_LAYER_NAME);
            int tillIndexStyle = layerIndex(STYLE_LAYERS[STYLE_LAYERS.length-1]);
            int tillIndex = Math.max(tillIndexContent, tillIndexStyle);*/
            ComputationGraph Graph = loadModel(false);
            INDArray generatedImage = initGeneratedImage();
            Map<String, INDArray> contentActivation = Graph.feedForward(contentImage, true);
            /*for (String s : ALL_LAYERS) {
                System.out.println(s);
                System.out.println(Arrays.toString(contentActivation.get(s).shape()));
            }*/
            Map<String, INDArray> styleActivation = Graph.feedForward(styleImage, true);
            HashMap<String, INDArray> styleActivationGram = initStyleGramMap(styleActivation);
            AdamUpdater optim = createAdamUpdater();
            for (int i = 0; i < ITERATIONS; i++) {
                if (i % 5 == 0) {
                    if ( i%25 == 0) {
                        BufferedImage output = fromMatrix(generatedImage);
                        URL resource = getClass().getResource(".");
                        File file = new File(resource.getPath() + "/iteration" + i + ".png");
                        ImageIO.write(output, "png", file);
                    }
                }
                Map<String, INDArray> forwardActivation = Graph.feedForward(new INDArray[] { generatedImage }, true, false);
                HashMap<String, INDArray> dropoutMasks = saveDropoutMasks(Graph);
                INDArray styleGrad = backPropStyles(Graph, styleActivationGram, forwardActivation, dropoutMasks);
                INDArray contentGrad = backPropContent(Graph, contentActivation, forwardActivation, dropoutMasks);
                INDArray totalGrad = contentGrad.muli(ALPHA).addi(styleGrad.muli(BETA));
                optim.applyUpdater(totalGrad, i, 0);
                generatedImage.subi(totalGrad);
                if (i % 5 == 0) {
                    double totalLoss = contentLoss(ALPHA, contentActivation.get(CONTENT_LAYER_NAME), forwardActivation.get(CONTENT_LAYER_NAME)) +
                            styleLoss(styleActivationGram, forwardActivation);
                    System.out.println("Loss: " + totalLoss);
                }
            }

            return fromMatrix(generatedImage);

            //return fromMatrix(mirrored(contentImage));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenerationException();
        }
    }

    protected HashMap<String, INDArray> saveDropoutMasks(ComputationGraph graph) {
        HashMap<String, INDArray> masks = new HashMap<>();
        for (String s : ALL_LAYERS) {
            Layer layer = graph.getLayer(s);
            if (layer!=null && (layer.type()==Layer.Type.CONVOLUTIONAL||layer.type()==Layer.Type.SUBSAMPLING)) {
                //if (s.equals("globalpooling")) break;
                INDArray dropoutMask = ((Dropout) ((AbstractLayer) layer).layerConf().getIDropout()).getMask();
                masks.put(s, dropoutMask);
            }
        }
        return masks;
    }

    protected AdamUpdater createAdamUpdater() {
        AdamUpdater adam = new AdamUpdater(new Adam(LEARNING_RATE, BETA1, BETA2, EPSILON));
        adam.setStateViewArray(Nd4j.zeros(1, 2 * CHANNELS * WIDTH * HEIGHT),
                new long[] {1, CHANNELS, HEIGHT, WIDTH}, 'c', true);
        return adam;
    }

    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        return null;
    }

    protected INDArray toMatrix(BufferedImage image) throws IOException {
        BufferedImage tmp = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        tmp.getGraphics().drawImage(image, 0, 0, null);
        System.out.println(tmp.getWidth() + " " + tmp.getHeight());
        INDArray imgMatrix = LOADER.asMatrix(Thumbnails.of(tmp).size(WIDTH, HEIGHT).asBufferedImage());
        System.out.println(Arrays.toString(imgMatrix.shape()));
        imgMatrix = imgMatrix.reshape(1, 3, WIDTH, HEIGHT);
        IMAGE_PREPROCESSOR.transform(imgMatrix);
        return imgMatrix;
    }

    protected BufferedImage fromMatrix(INDArray matrix) {
        long[] shape = matrix.shape();
        IMAGE_PREPROCESSOR.revertFeatures(matrix);
        long height = shape[2];
        long width = shape[3];
        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int red = matrix.getInt(0, 2, j, i);
                int green = matrix.getInt(0, 1, j, i);
                int blue = matrix.getInt(0, 0, j, i);

                red = Math.min(red, 255);
                green = Math.min(green, 255);
                blue = Math.min(blue, 255);

                red = Math.max(red, 0);
                green = Math.max(green, 0);
                blue = Math.max(blue, 0);
                image.setRGB(i, j, new Color(red, green, blue).getRGB());
            }
        }
        return image;
    }

    protected double contentLoss(double contentWeight, INDArray contentActivation, INDArray forwardActivation) {
        return forwardActivation.squaredDistance(contentActivation) * contentWeight;
    }

    protected double styleLoss(HashMap<String, INDArray> styleActivationGram, Map<String, INDArray> forwardActivation) {
        double loss = 0.0;
        for (String s : STYLE_LAYERS) {
            String[] spl = s.split(",");
            INDArray styleGram = styleActivationGram.get(spl[0]);
            INDArray forwardGram = gramMatrix(forwardActivation.get(spl[0]), true);
            loss += forwardGram.squaredDistance(styleGram) * Double.parseDouble(spl[1]);
        }
        return loss * BETA;
    }

    protected INDArray gramMatrix(INDArray activation, boolean normalize) {
        INDArray flat = flatten(activation);
        if (normalize) {
            return flat.mmul(flat.transpose()).divi(activation.shape()[3]*activation.shape()[1]*activation.shape()[2]);
        } else {
            return flat.mmul(flat.transpose());
        }
    }

    protected INDArray flatten(INDArray x) {
        long[] shape = x.shape();
        return x.reshape(shape[0] * shape[1], shape[2] * shape[3]);
    }

    protected INDArray initGeneratedImage() {
        int totalEntries = CHANNELS * HEIGHT * WIDTH;
        double[] result = new double[totalEntries];
        for (int i = 0; i < result.length; i++) {
            result[i] = ThreadLocalRandom.current().nextDouble(-20, 20);
        }
        INDArray randomMatrix = Nd4j.create(result, new int[]{1, CHANNELS, HEIGHT, WIDTH});
        return randomMatrix.muli(NOISE).addi(contentImage.muli(1-NOISE));
    }

    protected HashMap<String, INDArray> initStyleGramMap(Map<String, INDArray> styleActivation) {
        HashMap<String, INDArray> gramMap = new HashMap<>();
        for (String s : STYLE_LAYERS) {
            String[] spl = s.split(",");
            String styleLayerName = spl[0];
            INDArray activation = styleActivation.get(styleLayerName);
            gramMap.put(styleLayerName, gramMatrix(activation, true));
        }
        return gramMap;
    }

    protected INDArray backPropStyles(ComputationGraph graph, HashMap<String, INDArray> gramActivations, Map<String, INDArray> forwardActivations, HashMap<String, INDArray> dropoutMasks) {
        INDArray backProp = Nd4j.zeros(1, CHANNELS, HEIGHT, WIDTH);
        for (String s : STYLE_LAYERS) {
            String[] spl = s.split(",");
            String layerName = spl[0];
            double weight = Double.parseDouble(spl[1]);
            INDArray gramActivation = gramActivations.get(layerName);
            INDArray forwardActivation = forwardActivations.get(layerName);
            int index = layerIndex(layerName);
            INDArray derivativeStyle = derivStyleLossInLayer(gramActivation, forwardActivation).transpose();
            backProp.addi(backPropagate(graph, derivativeStyle.reshape(forwardActivation.shape()), index, dropoutMasks).muli(weight));
        }
        return backProp;
    }

    protected INDArray derivStyleLossInLayer(INDArray gramFeatures, INDArray targetFeatures) {
        targetFeatures = targetFeatures.dup();
        double N = targetFeatures.shape()[0];
        double M = targetFeatures.shape()[1] * targetFeatures.shape()[2];

        double styleWeight = 1 / (N * N * M * M);

        // G^l
        INDArray contentGram = gramMatrix(targetFeatures, true);

        // G^l - A^l
        INDArray diff = contentGram.sub(gramFeatures);

        // (F^l)^T * (G^l - A^l)
        INDArray fTranspose = flatten(targetFeatures).transpose();
        INDArray fTmulGA = fTranspose.mmul(diff);

        // divide by weight
        INDArray derivative = fTmulGA.muli(styleWeight);

        return derivative.muli(checkPositive(fTranspose));
    }

    protected INDArray backPropContent(ComputationGraph graph, Map<String, INDArray> contentActivations, Map<String, INDArray> forwardActivations, HashMap<String, INDArray> dropoutMasks) {
        INDArray contentActivation = contentActivations.get(CONTENT_LAYER_NAME);
        INDArray forwardActivation = forwardActivations.get(CONTENT_LAYER_NAME);
        INDArray derivativeContent = derivContentLossInLayer(contentActivation, forwardActivation);
        return backPropagate(graph, derivativeContent.reshape(forwardActivation.shape()), layerIndex(CONTENT_LAYER_NAME), dropoutMasks);
    }

    protected INDArray derivContentLossInLayer(INDArray contentFeatures, INDArray targetFeatures) {
        targetFeatures = targetFeatures.dup();
        contentFeatures = contentFeatures.dup();
        double C = targetFeatures.shape()[0];
        double W = targetFeatures.shape()[1];
        double H = targetFeatures.shape()[2];
        //System.out.println(Arrays.toString(targetFeatures.shape()));
        double contentWeight = 1.0 / (2 * C * H * W);
        INDArray derivative = targetFeatures.sub(contentFeatures);
        return flatten(derivative.muli(contentWeight).muli(checkPositive(targetFeatures)));
    }

    protected INDArray checkPositive(INDArray matrix) {
        BooleanIndexing.applyWhere(matrix, Conditions.lessThan(0.0f), new Value(0.0f));
        BooleanIndexing.applyWhere(matrix, Conditions.greaterThan(0.0f), new Value(1.0f));
        return matrix;
    }

    protected int layerIndex(String layerName) {
        for (int i = 0; i < ALL_LAYERS.length; i++) {
            if (layerName.equalsIgnoreCase(ALL_LAYERS[i])) return i;
        }
        return -1;
    }

    protected INDArray backPropagate(ComputationGraph graph, INDArray dLdA, int startIndex, HashMap<String, INDArray> masks) {
        for (int i = startIndex; i > 0; i--) {
            Layer layer = graph.getLayer(ALL_LAYERS[i]);
            dLdA = layer.backpropGradient(dLdA, LayerWorkspaceMgr.noWorkspaces()).getSecond();
        }
        return dLdA;
    }

}
