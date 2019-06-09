package model.repositories;

import server.ServerManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static util.Constants.NUM_STYLE_IMAGES;

public class StyleRepo {
    private StyleRepo(){}
    public static void init(){}
    private static BufferedImage styleImages[] = new BufferedImage[NUM_STYLE_IMAGES];
    static {
        styleImages[0] = getImage("/TestImages/img1.png");
        styleImages[1] = getImage("/TestImages/la_muse.png");
        styleImages[2] = getImage("/TestImages/rain_princess.png");
        styleImages[3] = getImage("/TestImages/udnie.png");
        styleImages[4] = getImage("/TestImages/starry_night.png");
        styleImages[5] = getImage("/TestImages/the_scream.png");
        styleImages[6] = getImage("/TestImages/composition_vii.png");
    }

    private static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(ServerManager.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static BufferedImage getStyle(int styleID){
        try {
            return styleImages[styleID];
        } catch (ArrayIndexOutOfBoundsException ae) {
            return styleImages[styleID%NUM_STYLE_IMAGES];
        }
    }

}
