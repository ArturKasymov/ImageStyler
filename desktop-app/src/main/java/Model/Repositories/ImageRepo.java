package Model.Repositories;

import Utils.GenerationException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.coobird.thumbnailator.Thumbnails;
import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.model.SqueezeNet;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.deeplearning4j.zoo.ZooModel;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.indexing.functions.Value;
import org.nd4j.linalg.learning.AdamUpdater;
import org.nd4j.linalg.learning.config.Adam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ImageRepo {
    protected static final Logger log = LoggerFactory.getLogger(ImageRepo.class);
    private INDArray contentImage;
    private INDArray styleImage;

    private int INITIAL_HEIGHT;
    private int INITIAL_WIDTH;

    private final static int HEIGHT = 224;
    private final static int WIDTH = 224;
    private final static int CHANNELS = 3;

    private final static double LEARNING_RATE = 2;
    private static final double BETA1 = 0.8;
    private static final double BETA2 = 0.999;
    private static final double EPSILON = 0.00000008;
    private static final double NOISE = 0.1;

    private static final int ITERATIONS = 5;

    private static final String[] ALL_LAYERS = new String[]{
            "input_1",
            "block1_conv1",
            "block1_conv2",
            "block1_pool",
            "block2_conv1",
            "block2_conv2",
            "block2_pool",
            "block3_conv1",
            "block3_conv2",
            "block3_conv3",
            "block3_pool",
            "block4_conv1",
            "block4_conv2",
            "block4_conv3",
            "block4_pool",
            "block5_conv1",
            "block5_conv2",
            "block5_conv3",
            "block5_pool",
            "flatten",
            "fc1",
            "fc2"
    };
    private static final String[] STYLE_LAYERS = new String[]{
            "block1_conv1,0.5",
            "block2_conv1,1.0",
            "block3_conv1,1.5",
            "block4_conv2,3.0",
            "block5_conv1,4.0"
    };
    private static final String CONTENT_LAYER_NAME = "block4_conv2";

    private static final double ALPHA = 0.025;
    private static final double BETA = 5.0;

    private final VGG16ImagePreProcessor IMAGE_PREPROCESSOR = new VGG16ImagePreProcessor();
    private final ImageLoader LOADER = new ImageLoader(HEIGHT, WIDTH, CHANNELS);

    public ImageRepo(Image contentImage, Image styleImage) {
        INITIAL_HEIGHT = (int) contentImage.getHeight();
        INITIAL_WIDTH = (int) contentImage.getWidth();
        try {
            this.contentImage = toMatrix(contentImage);
            this.styleImage = toMatrix(styleImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private INDArray mirrored(INDArray image) {
        return image.permute(0, 1, 3, 2);
    }

    public Image generate() throws GenerationException {
        try {
            ComputationGraph vgg16Graph = loadModel(false);
            INDArray generatedImage = initGeneratedImage();
            Map<String, INDArray> contentActivation = vgg16Graph.feedForward(contentImage, true);
            Map<String, INDArray> styleActivation = vgg16Graph.feedForward(styleImage, true);
            HashMap<String, INDArray> styleActivationGram = initStyleGramMap(styleActivation);
            AdamUpdater optim = createAdamUpdater();
            for (int i = 0; i < ITERATIONS; i++) {
                log.info("iteration " + i);
                Map<String, INDArray> forwardActivation = vgg16Graph.feedForward(new INDArray[] { generatedImage }, true, false);

                INDArray styleGrad = backPropStyles(vgg16Graph, styleActivationGram, forwardActivation);
                INDArray contentGrad = backPropContent(vgg16Graph, contentActivation, forwardActivation);
                INDArray totalGrad = contentGrad.muli(ALPHA).addi(styleGrad.muli(BETA));
                optim.applyUpdater(totalGrad, i, 0);
                generatedImage.subi(totalGrad);

                // TODO: log total loss

            }

            return fromMatrix(generatedImage);
            //return fromMatrix(mirrored(contentImage));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenerationException();
        }
    }

    private AdamUpdater createAdamUpdater() {
        AdamUpdater adam = new AdamUpdater(new Adam(LEARNING_RATE, BETA1, BETA2, EPSILON));
        adam.setStateViewArray(Nd4j.zeros(1, 2* CHANNELS * WIDTH * HEIGHT),
                new long[] {1, CHANNELS, HEIGHT, WIDTH}, 'c', true);
        return adam;
    }

    private ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = VGG16.builder().build();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        vgg16.initGradientsView();
        if (logIt) log.info(vgg16.summary());
        return vgg16;
    }

    private INDArray toMatrix(Image image) throws IOException {
        BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
        BufferedImage tmp = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        tmp.getGraphics().drawImage(temp, 0, 0, null);
        //INDArray imgMatrix = LOADER.asMatrix(tmp);
        INDArray imgMatrix = LOADER.asMatrix(Thumbnails.of(tmp).size(WIDTH, HEIGHT).asBufferedImage());
        imgMatrix = imgMatrix.reshape(1, 3, WIDTH, HEIGHT);
        IMAGE_PREPROCESSOR.transform(imgMatrix);
        return imgMatrix;
    }

    private Image fromMatrix(INDArray matrix) {
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
        return SwingFXUtils.toFXImage(image, null);
    }

    private INDArray gramMatrix(INDArray activation, boolean normalize) {
        INDArray flat = flatten(activation);
        if (normalize) {
            return flat.mmul(flat.transpose()).divi(activation.shape()[3]*activation.shape()[1]*activation.shape()[2]);
        } else {
            return flat.mmul(flat.transpose());
        }
    }

    private INDArray flatten(INDArray x) {
        long[] shape = x.shape();
        return x.reshape(shape[0] * shape[1], shape[2] * shape[3]);
    }

    private INDArray initGeneratedImage() {
        int totalEntries = CHANNELS * HEIGHT * WIDTH;
        double[] result = new double[totalEntries];
        for (int i = 0; i < result.length; i++) {
            result[i] = ThreadLocalRandom.current().nextDouble(-20, 20);
        }
        INDArray randomMatrix = Nd4j.create(result, new int[]{1, CHANNELS, HEIGHT, WIDTH});
        return randomMatrix.muli(NOISE).addi(contentImage.muli(1 - NOISE));
    }

    private HashMap<String, INDArray> initStyleGramMap(Map<String, INDArray> styleActivation) {
        HashMap<String, INDArray> gramMap = new HashMap<>();
        for (String s : STYLE_LAYERS) {
            String[] spl = s.split(",");
            String styleLayerName = spl[0];
            INDArray activation = styleActivation.get(styleLayerName);
            gramMap.put(styleLayerName, gramMatrix(activation, false));
        }
        return gramMap;
    }

    private INDArray backPropStyles(ComputationGraph graph, HashMap<String, INDArray> gramActivations, Map<String, INDArray> forwardActivations) {
        INDArray backProp = Nd4j.zeros(1, CHANNELS, HEIGHT, WIDTH);
        for (String s : STYLE_LAYERS) {
            String[] spl = s.split(",");
            String layerName = spl[0];
            double weight = Double.parseDouble(spl[1]);
            INDArray gramActivation = gramActivations.get(layerName);
            INDArray forwardActivation = forwardActivations.get(layerName);
            int index = layerIndex(layerName);
            INDArray derivativeStyle = derivStyleLossInLayer(gramActivation, forwardActivation).transpose();
            backProp.addi(backPropagate(graph, derivativeStyle.reshape(forwardActivation.shape()), index).muli(weight));
        }
        return backProp;
    }

    private INDArray derivStyleLossInLayer(INDArray gramFeatures, INDArray targetFeatures) {
        targetFeatures = targetFeatures.dup();
        double N = targetFeatures.shape()[0];
        double M = targetFeatures.shape()[1] * targetFeatures.shape()[2];

        double styleWeight = 1 / (N * N * M * M);

        // G^l
        INDArray contentGram = gramMatrix(targetFeatures, false);

        // G^l - A^l
        INDArray diff = contentGram.sub(gramFeatures);

        // (F^l)^T * (G^l - A^l)
        INDArray fTranspose = flatten(targetFeatures).transpose();
        INDArray fTmulGA = fTranspose.mmul(diff);

        // divide by weight
        INDArray derivative = fTmulGA.muli(styleWeight);

        return derivative.muli(checkPositive(fTranspose));
    }

    private INDArray backPropContent(ComputationGraph graph, Map<String, INDArray> contentActivations, Map<String, INDArray> forwardActivations) {
        INDArray contentActivation = contentActivations.get(CONTENT_LAYER_NAME);
        INDArray forwardActivation = forwardActivations.get(CONTENT_LAYER_NAME);
        INDArray derivativeContent = derivContentLossInLayer(contentActivation, forwardActivation);
        return backPropagate(graph, derivativeContent.reshape(forwardActivation.shape()), layerIndex(CONTENT_LAYER_NAME));
    }

    private INDArray derivContentLossInLayer(INDArray contentFeatures, INDArray targetFeatures) {
        targetFeatures = targetFeatures.dup();
        contentFeatures = contentFeatures.dup();
        double C = targetFeatures.shape()[0];
        double W = targetFeatures.shape()[1];
        double H = targetFeatures.shape()[2];

        double contentWeight = 1.0 / (2 * C * H * W);
        INDArray derivative = targetFeatures.sub(contentFeatures);
        return flatten(derivative.muli(contentWeight).muli(checkPositive(targetFeatures)));
    }

    private INDArray checkPositive(INDArray matrix) {
        BooleanIndexing.applyWhere(matrix, Conditions.lessThan(0.0f), new Value(0.0f));
        BooleanIndexing.applyWhere(matrix, Conditions.greaterThan(0.0f), new Value(1.0f));
        return matrix;
    }

    private int layerIndex(String layerName) {
        for (int i = 0; i < ALL_LAYERS.length; i++) {
            if (layerName.equalsIgnoreCase(ALL_LAYERS[i])) return i;
        }
        return -1;
    }

    private INDArray backPropagate(ComputationGraph graph, INDArray dLdA, int startIndex) {
        for (int i = startIndex; i > 0; i--) {
            Layer layer = graph.getLayer(ALL_LAYERS[i]);
            dLdA = layer.backpropGradient(dLdA, LayerWorkspaceMgr.noWorkspaces()).getSecond();
        }
        return dLdA;
    }

}
