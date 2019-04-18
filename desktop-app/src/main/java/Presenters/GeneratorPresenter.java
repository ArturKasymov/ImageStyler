package Presenters;

import Model.Interactors.GeneratorInteractor;
import Model.Interactors.Interactor;
import Views.Implementations.GeneratorViewImpl;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.Date;

public class GeneratorPresenter {

    private GeneratorViewImpl view;
    private GeneratorInteractor interactor;
    public GeneratorPresenter(GeneratorViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void generate() {}

    public void saveGeneratedImage(Image image, String photoName, Date date) {

    }
}
