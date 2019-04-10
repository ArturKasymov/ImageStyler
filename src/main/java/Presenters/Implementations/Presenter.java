package Presenters.Implementations;

import Presenters.Interfaces.LoginPresenter;
import Presenters.Interfaces.RegisterPresenter;
import Views.LoginView;
import javafx.scene.Parent;

public class Presenter implements LoginPresenter, RegisterPresenter {
    private Parent view;

    public Presenter(Parent view) {
        this.view = view;
    }

    public void login(CharSequence login, CharSequence password) {

    }

    public void register(CharSequence login, CharSequence password) {

    }

}
