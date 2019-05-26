package Views.Implementations;

import Model.Interactors.Interactor;
import Model.Interactors.MainInteractor;
import Presenters.LoginPresenter;

import Utils.controls.RingAnimation;
import javafx.application.Platform;
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
import javafx.scene.layout.VBox;


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
    private VBox root;

    @FXML
    private VBox form;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label warning;

    @FXML
    private Button reconnectButton;

    private boolean beingAnimated = false;

    private RingAnimation indicator = new RingAnimation();

    private boolean filledIn() {
        return loginField.getCharacters().length()>0 && passwordField.getCharacters().length()>0;
    }

    private void buttonToggle() {
        loginButton.setDisable(!(filledIn() && presenter.checkConnection()));
    }

    private void maybeLogin(KeyEvent event) {
        if (event==null || (event.getCode().getName().equals("Enter") && filledIn() && presenter.checkConnection())) {
            loginButton.setDisable(true);
            registerButton.setDisable(true);
            presenter.login(loginField.getCharacters(), passwordField.getCharacters());
            root.requestFocus();
        }

    }

    @FXML
    public void initialize() {
        presenter.initCallback();

        warning.setVisible(false);

        loginButton.setDisable(true);

        loginField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        loginField.setOnKeyPressed(event -> maybeLogin(event));

        passwordField.setOnKeyPressed(event -> maybeLogin(event));

    }

    @FXML
    protected void onLogin(ActionEvent e) {
        maybeLogin(null);
    }

    @FXML
    protected void onMoveToRegister(ActionEvent e) {
        presenter.register();
    }

    @Override
    public void showWrongDataAlert() {
        //warning.setVisible(true);
        if(getViewID()==getAppManager().getCurrentView()){
            loginField.getStyleClass().add("wrong-alert");
            passwordField.getStyleClass().add("wrong-alert");
            loginButton.setDisable(false);
            registerButton.setDisable(false);
            loginField.setOnMouseClicked(event -> hideWrongDataAlert());
            passwordField.setOnMouseClicked(event -> hideWrongDataAlert());
            //loginField.requestFocus();
        }
    }

    @Override
    public void initViewData() {
        if(!presenter.checkConnection())showReconnectButton();
    }

    private void hideWrongDataAlert() {
        //warning.setVisible(false);
        loginField.getStyleClass().remove("wrong-alert");
        passwordField.getStyleClass().remove("wrong-alert");
    }

    public void goToRegister(){
        hideWrongDataAlert();
        changeViewTo(new RegisterViewImpl());
    }

    public void goToMain(){
        //presenter.unsubscribe();
        hideWrongDataAlert();
        changeViewTo(new MainViewImpl());
        loginField.clear();
        passwordField.clear();
        loginButton.setDisable(false);
        registerButton.setDisable(false);
    }

    @Override
    public void showReconnectButton() {
        if(checkCurrentView()) {
            reconnectButton.setDisable(false);
            reconnectButton.setOnMouseClicked(event -> {
                presenter.reconnect();
                loginButton.setDisable(false);
                registerButton.setDisable(false);
                reconnectButton.setDisable(true);
            });
        }
    }

    @Override
    public void setAnimation(boolean set) {
        beingAnimated = set;
        if (set) {
            root.getChildren().setAll(indicator);
        } else {
            root.getChildren().setAll(form, reconnectButton);
        }
    }
}
