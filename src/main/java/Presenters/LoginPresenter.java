package Presenters;

import Views.LoginViewImpl;

public class LoginPresenter {
    private LoginViewImpl view;

    public LoginPresenter(LoginViewImpl view) {
        this.view = view;
    }

    public void login(CharSequence login, CharSequence password) {

    }

    public void register(CharSequence login, CharSequence password) {

    }

    public void unsubscribe(){
        this.view=null;
    }
}
