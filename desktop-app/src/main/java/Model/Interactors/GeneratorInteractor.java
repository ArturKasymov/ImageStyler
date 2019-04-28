package Model.Interactors;

import Model.Database.Entity.UserImage;
import Utils.GenerationException;
import javafx.scene.image.Image;

import java.util.Date;

public interface GeneratorInteractor {
    UserImage insertUserImage(Image image, String name, Date date);
    Image generate(Image contentImage, Image styleImage) throws GenerationException;
}
