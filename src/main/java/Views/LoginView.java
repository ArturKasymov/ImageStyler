package Views;

import Presenters.Interfaces.LoginPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginView {
    private LoginPresenter presenter;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    public void initialize() {
        loginButton.setDisable(true);
        // TODO: set-on eventListeners
    }

}
