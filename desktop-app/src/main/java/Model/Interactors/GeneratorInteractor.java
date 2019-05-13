package Model.Interactors;

import Model.Database.Entity.UserImage;
import Model.Repositories.Generation.core.GenerationException;
import Utils.Constants;
import javafx.scene.image.Image;

import java.util.Date;

public interface GeneratorInteractor {
    UserImage insertUserImage(Image image, String name, Date date);
    Image generate(Image contentImage, Image styleImage, Constants.NEURAL_NET net) throws GenerationException;
}
