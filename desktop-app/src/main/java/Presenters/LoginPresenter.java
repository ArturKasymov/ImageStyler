package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.LoginInteractor;
import Views.Implementations.LoginViewImpl;


public class LoginPresenter {
    private LoginViewImpl view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginViewImpl view) {
        this.view=view;
        this.interactor=Interactor.getInstance();
    }

    public void login(CharSequence login, CharSequence password) {
        if (interactor.checkLoginData(login, password)){
            //ToDo add animation while getting data from server + make MultiThread
            interactor.checkUserData();

            view.goToMain();
        }
        else view.showWrongDataAlert();
    }

    public void register() {
        view.goToRegister();
    }

    public void unsubscribe(){
        this.view=null;
    }
}
