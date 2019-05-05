package Model.Repositories.Generation.BaseGeneration.DarkNet;

import Model.Repositories.Generation.core.BaseGenerationRepo;
import javafx.scene.image.Image;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.Darknet19;

import java.io.IOException;

public class DarkNetGenerator extends BaseGenerationRepo {
    public DarkNetGenerator(Image contentImage, Image styleImage) {
        super(contentImage, styleImage);
    }

    @Override
    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = Darknet19.builder().workspaceMode(WorkspaceMode.NONE).build();
        zooModel.setInputShape(new int[][] {{3, 224, 224}});
        ComputationGraph squeezeNet = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        squeezeNet.initGradientsView();
        if (logIt) log.info(squeezeNet.summary());
        return squeezeNet;
    }

    @Override
    protected void initHyperParams() {
        IMAGE_PREPROCESSOR = DarkNetHyperParameters.IMAGE_PREPROCESSOR;
        HEIGHT = DarkNetHyperParameters.HEIGHT;
        WIDTH = DarkNetHyperParameters.WIDTH;

        ALL_LAYERS = DarkNetHyperParameters.ALL_LAYERS;
        STYLE_LAYERS = DarkNetHyperParameters.STYLE_LAYERS;
        CONTENT_LAYER_NAME = DarkNetHyperParameters.CONTENT_LAYER_NAME;

        ITERATIONS = DarkNetHyperParameters.ITERATIONS;
        ALPHA = DarkNetHyperParameters.ALPHA;
        BETA = DarkNetHyperParameters.BETA;

        LEARNING_RATE = DarkNetHyperParameters.LEARNING_RATE;
        BETA1 = DarkNetHyperParameters.BETA1;
        BETA2 = DarkNetHyperParameters.BETA2;
        EPSILON = DarkNetHyperParameters.EPSILON;
        NOISE = DarkNetHyperParameters.NOISE;
    }
}
