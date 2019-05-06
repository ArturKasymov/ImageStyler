package Model.Repositories.Generation.BaseGeneration.VGG16;

import org.deeplearning4j.zoo.ZooModel;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;


public class VGG16HyperParameters {

    public static final double LEARNING_RATE = 2;
    public static final double BETA1 = 0.8;
    public static final double BETA2 = 0.999;
    public static final double EPSILON = 0.00000008;
    public static final double NOISE = 0.1;

    public static final double ALPHA = 0.025;
    public static final double BETA = 5.0;

    public static final int ITERATIONS = 1000;

    public static final String[] ALL_LAYERS = new String[]{
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
    public static final String[] STYLE_LAYERS = new String[]{
            "block1_conv1,0.5",
            "block2_conv1,1.0",
            "block3_conv1,1.5",
            "block4_conv2,3.0",
            "block5_conv1,4.0"
    };
    public static final String CONTENT_LAYER_NAME = "block4_conv2";

    public static final int HEIGHT = 224;
    public static final int WIDTH = 224;

    public static final VGG16ImagePreProcessor IMAGE_PREPROCESSOR = new VGG16ImagePreProcessor();
}
