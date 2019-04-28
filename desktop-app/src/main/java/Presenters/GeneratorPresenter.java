package Presenters;

import Model.Interactors.GeneratorInteractor;
import Model.Interactors.Interactor;
import Utils.GenerationException;
import Views.Implementations.GeneratorViewImpl;
import javafx.scene.image.Image;

import java.util.Date;

public class GeneratorPresenter {

    private GeneratorViewImpl view;
    private GeneratorInteractor interactor;
    public GeneratorPresenter(GeneratorViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void generate(Image contentImage, Image styleImage) {
        try {
            view.setGeneratedImageView(interactor.generate(contentImage, styleImage));
        } catch (GenerationException e) {
            e.printStackTrace();
        }
    }

    public void saveGeneratedImage(Image image, String photoName, Date date) {
        view.notifyList(interactor.insertUserImage(image, photoName, date));
    }
}
