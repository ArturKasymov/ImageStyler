package Model.Repositories;

import javafx.scene.image.Image;

public class ImageRepo {
    private Image contentImage;
    private Image styleImage;
    public ImageRepo(Image contentImage, Image styleImage) {
        this.contentImage = contentImage;
        this.styleImage = styleImage;
    }
    public Image generate() {
        return contentImage;
    }
}
