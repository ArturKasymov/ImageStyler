package Views.Implementations;

import Presenters.LoginPresenter;
import Views.BaseView;
import Views.Interfaces.LoginView;
import Views.core.ViewByID;
import app.ViewManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


public class LoginViewImpl extends BaseView implements LoginView {

    private LoginPresenter presenter;
    public LoginViewImpl() {
        this.presenter = new LoginPresenter(this);
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
    private Hyperlink registerLink;

    private boolean filledIn() {
        return loginField.getCharacters().length()>0 && passwordField.getCharacters().length()>0;
    }

    @FXML
    public void initialize() {
        loginButton.setDisable(true);

        loginField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (filledIn()) loginButton.setDisable(false);
                else loginButton.setDisable(true);
            }
        });

        passwordField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (filledIn()) loginButton.setDisable(false);
                else loginButton.setDisable(true);
            }
        });

        loginField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode().getName()=="Enter" && filledIn())
                    presenter.login(loginField.getCharacters(), passwordField.getCharacters());
            }
        });

        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode().getName()=="Enter" && filledIn())
                    presenter.login(loginField.getCharacters(), passwordField.getCharacters());
            }
        });

        loginButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                presenter.login(loginField.getCharacters(), passwordField.getCharacters());
            }
        });

        registerLink.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                presenter.register();
                registerLink.setVisited(false);
            }
        });


    }

    public void showWrongDataAlert(){

    }

    public void goToRegister(){
        presenter.unsubscribe();
        changeViewTo(new RegisterViewImpl());
    }

    public void goToMain(){
        presenter.unsubscribe();
        changeViewTo(new MainViewImpl());
    }

}
