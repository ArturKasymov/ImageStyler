package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.RegisterInteractor;
import Presenters.Callbacks.RegisterCallback;
import Views.Interfaces.RegisterView;
import javafx.application.Platform;


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
            view.showAlert();
            view.setAnimation(false);
        });
    }

    @Override
    public void goToMain() {
        interactor.checkUserDirectory();
        Platform.runLater(()->{
            view.goToMain();
            view.setAnimation(false);
        });
    }

    @Override
    public void failedConnect() {
        view.showReconnectButton();
    }

    public void initCallback(){
        interactor.initRegisterCallback(this);
    }

    public void reconnect() {
        interactor.reconnect();
    }

    public boolean checkConnection() {
        return interactor.checkConnection();
    }
}
