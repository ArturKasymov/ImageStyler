package Views.Implementations;

import Presenters.RegisterPresenter;
import Views.core.BaseView;
import Views.Interfaces.RegisterView;
import Views.core.ViewByID;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;


public class RegisterViewImpl extends BaseView implements RegisterView {
    private RegisterPresenter presenter;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField onceMorePasswordField;

    @FXML
    private Button registerButton;

    private boolean filledIn() {
        return loginField.getCharacters().length()>0 && passwordField.getCharacters().length()>0 && onceMorePasswordField.getCharacters().length()>0;
    }

    private boolean passwordsMatch() {
        return passwordField.getCharacters().toString().equals(onceMorePasswordField.getCharacters().toString());
    }

    @Override
    public ViewByID getViewID() {
        return ViewByID.REGISTER_VIEW;
    }

    public RegisterViewImpl(){
        presenter= new RegisterPresenter(this);
    }


    public void goToMain(){
        //presenter.unsubscribe();
        changeViewTo(new MainViewImpl());
        registerButton.setDisable(false);
        loginField.clear();
        passwordField.clear();
        onceMorePasswordField.clear();
    }

    private void buttonToggle() {
        if (filledIn() && passwordsMatch()) registerButton.setDisable(false);
        else registerButton.setDisable(true);
    }

    private void maybeLogin(KeyEvent event) {
        if (event==null || (event.getCode().getName().equals("Enter") && filledIn() && passwordsMatch())) {
            registerButton.setDisable(false);
            presenter.register(loginField.getCharacters(), passwordField.getCharacters());
        }
    }

    @FXML
    public void initialize() {
        presenter.initCallback();
        registerButton.setDisable(true);

        loginField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        onceMorePasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        loginField.setOnKeyPressed(event -> maybeLogin(event));

        passwordField.setOnKeyPressed(event -> maybeLogin(event));

        onceMorePasswordField.setOnKeyPressed(event -> maybeLogin(event));
    }

    @FXML
    protected void onRegister(ActionEvent e) {
        maybeLogin(null);
    }

    @Override
    public void showAlert() {
        //TODO show Alert
        registerButton.setDisable(false);
        loginField.styleProperty().setValue("-fx-border-color: red;");
        passwordField.styleProperty().setValue("-fx-border-color: red;");
        onceMorePasswordField.styleProperty().setValue("-fx-border-color: red;");
        loginField.setOnMouseClicked(event -> loginField.styleProperty().setValue(""));
        passwordField.setOnMouseClicked(event -> passwordField.styleProperty().setValue(""));
        onceMorePasswordField.setOnMouseClicked(event -> onceMorePasswordField.styleProperty().setValue(""));
        System.out.println("Alert show");
        //throw new RuntimeException();
    }
}
