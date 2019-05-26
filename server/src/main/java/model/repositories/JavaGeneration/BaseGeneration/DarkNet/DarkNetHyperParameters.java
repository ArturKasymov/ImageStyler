package model.repositories.JavaGeneration.BaseGeneration.DarkNet;

import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

public class DarkNetHyperParameters {
    public static final double LEARNING_RATE = 2.5;
    public static final double BETA1 = 0.8;
    public static final double BETA2 = 0.999;
    public static final double EPSILON = 0.00000008;
    public static final double NOISE = 0.1;

    public static final double ALPHA = 0.025;
    public static final double BETA = 5.0;

    public static final int ITERATIONS = 300;

    public static final String[] ALL_LAYERS = new String[]{
            "input_1",
            "conv2d_1",
            "batch_normalization_1",
            "leaky_re_lu_1",
            "max_pooling2d_1",
            "conv2d_2",
            "batch_normalization_2",
            "leaky_re_lu_2",
            "max_pooling2d_2",
            "conv2d_3",
            "batch_normalization_3",
            "leaky_re_lu_3",
            "conv2d_4",
            "batch_normalization_4",
            "leaky_re_lu_4",
            "conv2d_5",
            "batch_normalization_5",
            "leaky_re_lu_5",
            "max_pooling2d_3",
            "conv2d_6",
            "batch_normalization_6",
            "leaky_re_lu_6",
            "conv2d_7",
            "batch_normalization_7",
            "leaky_re_lu_7",
            "conv2d_8",
            "batch_normalization_8",
            "leaky_re_lu_8",
            "max_pooling2d_4",
            "conv2d_9",
            "batch_normalization_9",
            "leaky_re_lu_9",
            "conv2d_10",
            "batch_normalization_10",
            "leaky_re_lu_10",
            "conv2d_11",
            "batch_normalization_11",
            "leaky_re_lu_11",
            "conv2d_12",
            "batch_normalization_12",
            "leaky_re_lu_12",
            "conv2d_13",
            "batch_normalization_13",
            "leaky_re_lu_13",
            "max_pooling2d_5",
            "conv2d_14",
            "batch_normalization_14",
            "leaky_re_lu_14",
            "conv2d_15",
            "batch_normalization_15",
            "leaky_re_lu_15",
            "conv2d_16",
            "batch_normalization_16",
            "leaky_re_lu_16",
            "conv2d_17",
            "batch_normalization_17",
            "leaky_re_lu_17",
            "conv2d_18",
            "batch_normalization_18",
            "leaky_re_lu_18",
            "conv2d_19",
            "globalpooling",
            "softmax",
            "loss"
    };
    public static final String[] STYLE_LAYERS = new String[]{

            "conv2d_4,1.0",
            "conv2d_9,2.0"

    };
    public static final String CONTENT_LAYER_NAME = "conv2d_10";

    public static final int HEIGHT = 224;
    public static final int WIDTH = 224;

    public static final DataNormalization IMAGE_PREPROCESSOR = new VGG16ImagePreProcessor();
}
