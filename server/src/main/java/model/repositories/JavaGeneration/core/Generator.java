package model.repositories.JavaGeneration.core;

import java.awt.image.BufferedImage;

public interface Generator {
    BufferedImage generate() throws GenerationException;
}
