package Model.Interactors;

import Presenters.Callbacks.GeneratorCallback;
import Utils.Constants;
import javafx.scene.image.Image;

public interface GeneratorInteractor {
    void generate(Image contentImage, int styleImageID, String imageName, Constants.NEURAL_NET net);
    void initGeneratorCallback(GeneratorCallback callback);
}
