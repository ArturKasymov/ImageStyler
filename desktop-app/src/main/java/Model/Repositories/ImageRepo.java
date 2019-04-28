package Model.Repositories;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.datavec.image.loader.ImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageRepo {
    private INDArray contentImage;
    private INDArray styleImage;

    private int HEIGHT;
    private int WIDTH;
    private final static int CHANNELS = 3;

    private final VGG16ImagePreProcessor IMAGE_PREPROCESSOR = new VGG16ImagePreProcessor();
    private final ImageLoader LOADER = new ImageLoader(HEIGHT,WIDTH,CHANNELS);

    public ImageRepo(Image contentImage, Image styleImage) {
        HEIGHT = (int) contentImage.getHeight();
        WIDTH = (int) contentImage.getWidth();
        this.contentImage = toMatrix(contentImage);
        this.styleImage = toMatrix(styleImage);
    }

    public Image generate() {
        return fromMatrix(contentImage);
    }

    private INDArray toMatrix(Image image) {
        BufferedImage temp = SwingFXUtils.fromFXImage(image, null);
        BufferedImage tmp = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        tmp.getGraphics().drawImage(temp, 0, 0, null);
        INDArray imgMatrix = LOADER.asMatrix(tmp);
        imgMatrix = imgMatrix.reshape(1, 3, 500, 500);
        IMAGE_PREPROCESSOR.transform(imgMatrix);
        return imgMatrix;
    }

    private Image fromMatrix(INDArray matrix) {
        long[] shape = matrix.shape();
        IMAGE_PREPROCESSOR.revertFeatures(matrix);
        long height = shape[2];
        long width = shape[3];
        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int red = matrix.getInt(0, 2, j, i);
                int green = matrix.getInt(0, 1, j, i);
                int blue = matrix.getInt(0, 0, j, i);

                red = Math.min(red, 255);
                green = Math.min(green, 255);
                blue = Math.min(blue, 255);

                red = Math.max(red, 0);
                green = Math.max(green, 0);
                blue = Math.max(blue, 0);
                image.setRGB(i, j, new Color(red, green, blue).getRGB());
            }
        }
        return SwingFXUtils.toFXImage(image, null);
    }
}
