package Presenters;

import Model.Interactors.GeneratorInteractor;
import Model.Interactors.Interactor;
import Views.Implementations.GeneratorViewImpl;

public class GeneratorPresenter {

    private GeneratorViewImpl view;
    private GeneratorInteractor interactor;
    public GeneratorPresenter(GeneratorViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void generate() {}
}
