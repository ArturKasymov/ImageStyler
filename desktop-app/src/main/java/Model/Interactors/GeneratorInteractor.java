package Model.Interactors;

import javafx.scene.image.Image;

import java.util.Date;

public interface GeneratorInteractor {
    void insertGeneratedImage(Image image, String name, Date date);
    Image generate(Image contentImage, Image styleImage);
}
