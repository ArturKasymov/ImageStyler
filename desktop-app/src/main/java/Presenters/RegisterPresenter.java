package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.RegisterInteractor;
import Views.Interfaces.RegisterView;

public class RegisterPresenter {

    private RegisterView view;
    private RegisterInteractor interactor;

    public RegisterPresenter(RegisterView view){
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void register(CharSequence login, CharSequence password){
        view.goToMain();
    }


    public void unsubscribe(){
        this.view=null;
    }
}
