package Presenters.Implementations;

import Presenters.Interfaces.LoginPresenter;
import Views.LoginView;

public class Presenter implements LoginPresenter {
    private LoginView view;

    public Presenter(LoginView view) {
        this.view = view;
    }

    public void login(CharSequence login, CharSequence password) {

    }

    public void setView(String v) {

    }

}
