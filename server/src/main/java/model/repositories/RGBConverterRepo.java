package model.repositories;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RGBConverterRepo {

    public static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }
        if (original.getType() == type) {
            return original;
        }
        BufferedImage image = new BufferedImage(original.getHeight(), original.getWidth(), type);
        Graphics2D g = image.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(original, 0, 0, null);
        }
        finally {
            g.dispose();
        }
        return image;
    }
}
