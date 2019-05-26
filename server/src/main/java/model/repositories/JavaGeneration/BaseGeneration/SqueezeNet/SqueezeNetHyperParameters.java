package model.repositories.JavaGeneration.BaseGeneration.SqueezeNet;

import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

public class SqueezeNetHyperParameters {
    public static final double LEARNING_RATE = 5.0;
    public static final double BETA1 = 0.8;
    public static final double BETA2 = 0.999;
    public static final double EPSILON = 0.00000008;
    public static final double NOISE = 0.1;

    public static final double ALPHA = 0.025;
    public static final double BETA = 5.0;

    public static final int ITERATIONS = 100;

    public static final String[] ALL_LAYERS = new String[]{
            "input_5",
            "conv1",
            "relu1",
            "pool1",
            "fire2_squeeze1x1",
            "fire2_relu_squeeze1x1",
            "fire2_expand1x1",
            "fire2_expand3x3",
            "fire2_relu_expand1x1",
            "fire2_relu_expand3x3",
            "fire2_concat",
            "fire3_squeeze1x1",
            "fire3_relu_squeeze1x1",
            "fire3_expand1x1",
            "fire3_expand3x3",
            "fire3_relu_expand1x1",
            "fire3_relu_expand3x3",
            "fire3_concat",
            "pool3",
            "fire4_squeeze1x1",
            "fire4_relu_squeeze1x1",
            "fire4_expand1x1",
            "fire4_expand3x3",
            "fire4_relu_expand1x1",
            "fire4_relu_expand3x3",
            "fire4_concat",
            "fire5_squeeze1x1",
            "fire5_relu_squeeze1x1",
            "fire5_expand1x1",
            "fire5_expand3x3",
            "fire5_relu_expand1x1",
            "fire5_relu_expand3x3",
            "fire5_concat",
            "pool5",
            "fire6_squeeze1x1",
            "fire6_relu_squeeze1x1",
            "fire6_expand1x1",
            "fire6_expand3x3",
            "fire6_relu_expand1x1",
            "fire6_relu_expand3x3",
            "fire6_concat",
            "fire7_squeeze1x1",
            "fire7_relu_squeeze1x1",
            "fire7_expand1x1",
            "fire7_expand3x3",
            "fire7_relu_expand1x1",
            "fire7_relu_expand3x3",
            "fire7_concat",
            "fire8_squeeze1x1",
            "fire8_relu_squeeze1x1",
            "fire8_expand1x1",
            "fire8_expand3x3",
            "fire8_relu_expand1x1",
            "fire8_relu_expand3x3",
            "fire8_concat",
            "fire9_squeeze1x1",
            "fire9_relu_squeeze1x1",
            "fire9_expand1x1",
            "fire9_expand3x3",
            "fire9_relu_expand1x1",
            "fire9_relu_expand3x3",
            "fire9_concat",
            "drop9",
            "conv10",
            "relu10",
            "global_average_pooling2d_5",
            "loss"
    };
    public static final String[] STYLE_LAYERS = new String[]{
            "relu1,10.0",
            "fire3_concat,15.0",
            "fire5_concat,20.0",
            "fire6_concat,20.0"
    };
    public static final String CONTENT_LAYER_NAME = "fire2_concat";

    public static final int HEIGHT = 227;
    public static final int WIDTH = 227;

    //public static final DataNormalization IMAGE_PREPROCESSOR = new NormalizerStandardize(Nd4j.create(
    //        new double[] {0.485, 0.456, 0.406}), Nd4j.create(new double[] {0.229, 0.224, 0.225}));

    public static final DataNormalization IMAGE_PREPROCESSOR = new VGG16ImagePreProcessor();
}
