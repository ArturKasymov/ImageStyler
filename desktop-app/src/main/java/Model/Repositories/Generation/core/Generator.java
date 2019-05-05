package Model.Repositories.Generation.core;

import javafx.scene.image.Image;

@FunctionalInterface
public interface Generator {
    Image generate() throws GenerationException;
}
