package Presenters;

import Model.Interactors.GeneratorInteractor;
import Model.Interactors.Interactor;
import Utils.Constants;
import Views.Implementations.GeneratorViewImpl;
import javafx.scene.image.Image;

public class GeneratorPresenter{

    private GeneratorViewImpl view;
    private GeneratorInteractor interactor;
    public GeneratorPresenter(GeneratorViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void generate(Image contentImage, int styleImageID, String imageName, Constants.NEURAL_NET net, double strength, boolean preserveSize) {
        interactor.generate(contentImage, styleImageID, imageName, net, strength, preserveSize);
    }
}
