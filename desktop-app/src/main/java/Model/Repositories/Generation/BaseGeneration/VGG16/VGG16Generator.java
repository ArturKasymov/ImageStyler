package Model.Repositories.Generation.BaseGeneration.VGG16;

import Model.Repositories.Generation.core.BaseGenerationRepo;
import javafx.scene.image.Image;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;

import java.io.IOException;

public class VGG16Generator extends BaseGenerationRepo {

    public VGG16Generator(Image contentImage, Image styleImage) {
        super(contentImage, styleImage);
    }

    @Override
    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = VGG16.builder().build();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        vgg16.initGradientsView();
        if (logIt) log.info(vgg16.summary());
        return vgg16;
    }

    @Override
    protected void initHyperParams() {
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
    }
}
