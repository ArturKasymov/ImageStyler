package Presenters;

import Views.Implementations.LoginViewImpl;

//for testing
import java.util.Random;

public class LoginPresenter {
    private LoginViewImpl view;

    public LoginPresenter(LoginViewImpl view) {
        this.view = view;
    }

    public void login(CharSequence login, CharSequence password) {
        if(new Random().nextBoolean()) view.goToMain();
        else view.showWrongDataAlert();
    }

    public void register() {
        view.goToRegister();
    }

    public void unsubscribe(){
        this.view=null;
    }
}
