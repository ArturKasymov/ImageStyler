package Model.Repositories.Generation.BaseGeneration.SqueezeNet;

import Model.Repositories.Generation.core.BaseGenerationRepo;
import javafx.scene.image.Image;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.SqueezeNet;
import org.nd4j.linalg.api.ops.executioner.OpExecutioner;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class SqueezeNetGenerator extends BaseGenerationRepo {

    public SqueezeNetGenerator(Image contentImage, Image styleImage) {
        super(contentImage, styleImage);
        Nd4j.getExecutioner().setProfilingMode(OpExecutioner.ProfilingMode.DISABLED);
    }

    @Override
    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = SqueezeNet.builder().workspaceMode(WorkspaceMode.NONE).build();
        ComputationGraph squeezeNet = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        squeezeNet.initGradientsView();
        if (logIt) log.info(squeezeNet.summary());
        return squeezeNet;
    }

    @Override
    protected void initHyperParams() {
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
}
