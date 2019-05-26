package model.repositories.JavaGeneration.BaseGeneration.DarkNet;

import model.repositories.JavaGeneration.core.BaseGenerationRepo;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.AbstractLayer;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.Darknet19;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class DarkNetGenerator extends BaseGenerationRepo {
    public DarkNetGenerator(BufferedImage contentImage, BufferedImage styleImage) {
        super(contentImage, styleImage);
    }

    @Override
    protected ComputationGraph loadModel(boolean logIt) throws IOException {
        ZooModel zooModel = Darknet19.builder().workspaceMode(WorkspaceMode.NONE).build();
        zooModel.setInputShape(new int[][] {{3, 224, 224}});
        ComputationGraph darkNet = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        darkNet.initGradientsView();
        if (logIt) log.info(darkNet.summary());
        return darkNet;
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
            dLdA = layer.backpropGradient(dLdA, LayerWorkspaceMgr.noWorkspaces()).getSecond();
        }
        return dLdA;
    }
}
