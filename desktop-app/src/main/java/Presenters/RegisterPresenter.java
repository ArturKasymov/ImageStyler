package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.RegisterInteractor;
import Presenters.Callbacks.RegisterCallback;
import Views.Interfaces.RegisterView;

public class RegisterPresenter implements RegisterCallback {

    private RegisterView view;
    private RegisterInteractor interactor;

    public RegisterPresenter(RegisterView view){
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void register(CharSequence login, CharSequence password){
        interactor.registerUser(login, password);
    }

    public void showAlert(){
        view.showAlert();
    }

    @Override
    public void goToMain() {
        interactor.checkUserDirectory();
        view.goToMain();
    }

    public void initCallback(){
        interactor.initRegisterCallback(this);
    }

    public void unsubscribe(){
        this.view=null;
    }
}
