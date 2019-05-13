package Model.Interactors;

import Model.Database.Entity.UserImage;
import Presenters.Callbacks.GeneratorCallback;
import Utils.Constants;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.Date;

public interface GeneratorInteractor {
    UserImage insertUserImage(int imageID, String name, Date date);
    void generate(Image contentImage, int styleImageID, String imageName, Constants.NEURAL_NET net);
    void initGeneratorCallback(GeneratorCallback callback);
    void saveUserImage(int imageID, BufferedImage image);
}
