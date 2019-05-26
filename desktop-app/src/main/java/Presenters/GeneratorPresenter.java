package Presenters;

import Model.Interactors.GeneratorInteractor;
import Model.Interactors.Interactor;
import Model.Repositories.Generation.core.GenerationException;
import Presenters.Callbacks.GeneratorCallback;
import Utils.Constants;
import Views.Implementations.GeneratorViewImpl;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.Date;

public class GeneratorPresenter implements GeneratorCallback {

    private GeneratorViewImpl view;
    private GeneratorInteractor interactor;
    public GeneratorPresenter(GeneratorViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void generate(Image contentImage, int styleImageID, String imageName, Constants.NEURAL_NET net, double strength) {
        interactor.generate(contentImage, styleImageID, imageName, net, strength);
    }

    public void initCallback(){
        interactor.initGeneratorCallback(this);
    }
}
