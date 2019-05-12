package Model.Repositories.Generation.PythonGeneration;

import Model.Repositories.Generation.core.GenerationException;
import Model.Repositories.Generation.core.Generator;
import app.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class PySqueezeNet implements Generator {

    Image contentImage;

    public PySqueezeNet(Image contentImage, Image styleImage) {
        this.contentImage = contentImage;
    }

    @Override
    public Image generate() throws GenerationException {
        URL url = Main.class.getResource("/PyGeneration/PyGenerationRepo/Main.py");
        try {
            System.out.println(url);
            Process p = Runtime.getRuntime().exec("py -3 " + url.toString().split("/")[1] + " > a.txt");
        } catch (IOException e) {e.printStackTrace();}
        return contentImage;
    }
}
