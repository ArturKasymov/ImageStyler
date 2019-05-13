package Presenters.Callbacks;

import java.awt.image.BufferedImage;
import java.util.Date;

public interface GeneratorCallback {
    void insertGeneratedImage(int imageID, String photoName, Date date);

    void saveGeneratedImage(int imageID, BufferedImage generatedImage);
}
