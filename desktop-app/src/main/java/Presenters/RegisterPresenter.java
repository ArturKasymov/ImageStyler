package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.RegisterInteractor;
import Presenters.Callbacks.RegisterCallback;
import Views.Interfaces.RegisterView;
import javafx.application.Platform;

import static Utils.Constants.DEFAULT_SERVER_IP;
import static Utils.Constants.DEFAULT_SERVER_PORT;

public class RegisterPresenter implements RegisterCallback {

    private RegisterView view;
    private RegisterInteractor interactor;

    public RegisterPresenter(RegisterView view){
        this.view = view;
        this.interactor= Interactor.getInstance();
    }

    public void register(CharSequence login, CharSequence password){
        interactor.registerUser(login, password);
        view.setAnimation(true);
    }

    public void showAlert(){
        Platform.runLater(()->{
            view.setAnimation(false);
            view.showAlert();
        });
    }

    @Override
    public void goToMain() {
        interactor.checkUserDirectory();
        view.goToMain();
        Platform.runLater(()->view.setAnimation(false));
    }

    @Override
    public void failedConnect() {
        view.showReconnectButton();
    }

    public void initCallback(){
        interactor.initRegisterCallback(this);
    }

    public void unsubscribe(){
        this.view=null;
    }

    public void reconnect() {
        Interactor.getInstance().startSessionManager(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT);
    }
}
