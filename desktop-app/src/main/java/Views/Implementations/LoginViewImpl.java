package Views.Implementations;

import Model.Interactors.Interactor;
import Model.Interactors.MainInteractor;
import Presenters.LoginPresenter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import Views.Interfaces.LoginView;
import Views.core.BaseView;
import Views.core.ViewByID;

import java.util.List;


public class LoginViewImpl extends BaseView implements LoginView {

    private LoginPresenter presenter;
    private MainInteractor interactor;
    public LoginViewImpl() {
        this.presenter = new LoginPresenter(this);
        this.interactor= Interactor.getInstance();
    }

    @Override
    public ViewByID getViewID() {
        return ViewByID.LOGIN_VIEW;
    }

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label warning;

    private boolean filledIn() {
        return loginField.getCharacters().length()>0 && passwordField.getCharacters().length()>0;
    }

    private void buttonToggle() {
        if (filledIn()) loginButton.setDisable(false);
        else loginButton.setDisable(true);
    }

    private void maybeLogin(KeyEvent event) {
        if (event==null || (event.getCode().getName().equals("Enter") && filledIn())) {
            presenter.login(loginField.getCharacters(), passwordField.getCharacters());
            loginField.clear();
            passwordField.clear();
        }
    }

    @FXML
    public void initialize() {
        warning.setVisible(false);
        loginButton.setDisable(true);

        loginField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                buttonToggle();
            }
        });

        passwordField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                buttonToggle();
            }
        });

        loginField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                maybeLogin(event);
            }
        });

        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                maybeLogin(event);
            }
        });

    }

    @FXML
    protected void onLogin(ActionEvent e) {
        maybeLogin(null);
    }

    @FXML
    protected void onMoveToRegister(ActionEvent e) {
        presenter.register();
    }

    public void showWrongDataAlert() {
        warning.setVisible(true);
    }

    public void goToRegister(){
        //presenter.unsubscribe();
        changeViewTo(new RegisterViewImpl());
    }

    public void goToMain(){
        //presenter.unsubscribe();
        changeViewTo(new MainViewImpl());
    }

}
