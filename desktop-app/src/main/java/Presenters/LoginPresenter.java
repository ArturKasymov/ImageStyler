package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.LoginInteractor;
import Presenters.Callbacks.LoginCallback;
import Views.Implementations.LoginViewImpl;


public class LoginPresenter implements LoginCallback{
    private LoginViewImpl view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginViewImpl view) {
        this.view=view;
        this.interactor=Interactor.getInstance();
    }

    public void login(CharSequence login, CharSequence password) {
        if (interactor.checkLoginData(login, password)){
            //ToDo add animation while getting data from server + make MultiThread



        }
    }

    public void initCallback(){
        interactor.initLoginCallback(this);
    }

    public void register() {
        view.goToRegister();
    }

    public void showWrongDataAlert(){
        view.showWrongDataAlert();
    }
    public void goToMain(){
        interactor.checkUserData();
        view.goToMain();
    }
}
