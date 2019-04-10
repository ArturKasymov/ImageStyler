package Views;

import Presenters.Implementations.Presenter;
import Presenters.Interfaces.LoginPresenter;
import app.ViewManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LoginView extends Parent {
    private LoginPresenter presenter;
    private final ViewManager app;

    public LoginView() {
        app = new ViewManager();
        this.presenter = new Presenter(this);
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
                app.setView(app.getRegisterView());
                registerLink.setVisited(false);
            }
        });

    }

    /*

    ???  onLogin()

    ???  onRegisterClick

    here?
     */

}
