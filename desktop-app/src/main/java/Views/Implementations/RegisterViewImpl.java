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
    }

    private void buttonToggle() {
        if (filledIn() && passwordsMatch()) registerButton.setDisable(false);
        else registerButton.setDisable(true);
    }

    private void maybeLogin(KeyEvent event) {
        if (event==null || (event.getCode().getName().equals("Enter") && filledIn() && passwordsMatch())) {
            registerButton.setDisable(false);
            presenter.register(loginField.getCharacters(), passwordField.getCharacters());
            loginField.clear();
            passwordField.clear();
            onceMorePasswordField.clear();
        }
    }

    @FXML
    public void initialize() {
        registerButton.setDisable(true);

        loginField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                buttonToggle();
            }
        });

        passwordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                buttonToggle();
            }
        });

        onceMorePasswordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                buttonToggle();
            }
        });

        loginField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                maybeLogin(event);
            }
        });

        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                maybeLogin(event);
            }
        });

        onceMorePasswordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                maybeLogin(event);
            }
        });

    }

    @FXML
    protected void onRegister(ActionEvent e) {
        maybeLogin(null);
    }

    @Override
    public void showAlert() {
        //TODO show Alert
        System.out.println("Alert show");
        //throw new RuntimeException();
    }
}
