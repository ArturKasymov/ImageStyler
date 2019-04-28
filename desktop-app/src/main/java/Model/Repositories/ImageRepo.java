package Model.Repositories;

import Utils.GenerationException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.deeplearning4j.zoo.ZooModel;
import org.nd4j.linalg.learning.AdamUpdater;
import org.nd4j.linalg.learning.config.Adam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageRepo {
    protected static final Logger log = LoggerFactory.getLogger(ImageRepo.class);
    private INDArray contentImage;
    private INDArray styleImage;

    private int HEIGHT;
    private int WIDTH;
    private final static int CHANNELS = 3;

    private final static double LEARNING_RATE = 2;
    private static final double BETA1 = 0.8;
    private static final double BETA2 = 0.999;
    private static final double EPSILON = 0.00000008;

    private static final int ITERATIONS = 1000;

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

    private final VGG16ImagePreProcessor IMAGE_PREPROCESSOR = new VGG16ImagePreProcessor();
    private final ImageLoader LOADER = new ImageLoader(HEIGHT, WIDTH, CHANNELS);

    public ImageRepo(Image contentImage, Image styleImage) {
        HEIGHT = (int) contentImage.getHeight();
        WIDTH = (int) contentImage.getWidth();
        this.contentImage = toMatrix(contentImage);
        this.styleImage = toMatrix(styleImage);
    }

    private INDArray mirrored(INDArray image) {
        return image.permute(0, 1, 3, 2);
    }

    public Image generate() throws GenerationException {
        try {
            /*
            ComputationGraph vgg16Graph = loadModel(false);
            INDArray generatedImage = initGeneratedImage(true);
            Map<String, INDArray> contentActivation = vgg16Graph.feedForward(contentImage, true);
            Map<String, INDArray> styleActivation = vgg16Graph.feedForward(styleImage, true);
            HashMap<String, INDArray> styleActivationGram = initStyleGramMap(styleActivation);
            AdamUpdater optim = createAdamUpdater();
            for (int i = 0; i < ITERATIONS; i++) {
                log.info("iteration " + i);

            }
            */
            //return fromMatrix(generatedImage);
            return fromMatrix(mirrored(contentImage));
        } catch (Exception e) {
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
        ZooModel zooModel = VGG16.builder().inputShape(new int[] {CHANNELS, HEIGHT, WIDTH}).build();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        vgg16.initGradientsView();
        if (logIt) log.info(vgg16.summary());
        return vgg16;
    }

    private INDArray toMatrix(Image image) {
        BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
        BufferedImage tmp = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        tmp.getGraphics().drawImage(temp, 0, 0, null);
        INDArray imgMatrix = LOADER.asMatrix(tmp);
        //INDArray imgMatrix = LOADER.asMatrix(Thumbnails.of(tmp).size(WIDTH, HEIGHT).asBufferedImage());
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

    private INDArray initGeneratedImage(boolean random) {
        if (random) {
            return Nd4j.rand(contentImage.shape());
        } else {
            return contentImage;
        }
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


}
