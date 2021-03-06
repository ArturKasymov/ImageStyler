package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.LoginInteractor;
import Presenters.Callbacks.LoginCallback;
import Views.Implementations.LoginViewImpl;
import Views.Interfaces.LoginView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Date;


public class LoginPresenter implements LoginCallback{
    private LoginView view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginViewImpl view) {
        this.view=view;
        this.interactor=Interactor.getInstance();
    }

    public void login(CharSequence login, CharSequence password) {
        try {
            interactor.login(login, password);
            view.setAnimation(true);
        } catch (NullPointerException e){ }
    }

    public void initCallback(){
        interactor.initLoginCallback(this);
    }

    public void register() {
        view.goToRegister();
    }

    public void showWrongDataAlert(){
        Platform.runLater(()->{
            view.showWrongDataAlert();
            view.setAnimation(false);
        });
    }
    public void goToMain(){
        interactor.checkUserData();

        Platform.runLater(()->{
            view.goToMain();
            view.setAnimation(false);
        });
    }

    @Override
    public ArrayList<Integer> getCachedImagesID(int userID) {
        return interactor.getUserCachedImagesID(userID);
    }

    @Override
    public void insertUserImage(int imageID, String name, Date date) {
        interactor.insertUserImage(imageID,name,date,false);
    }

    @Override
    public void deleteImage(int imageID) {
        interactor.deleteLocalImage(imageID);
    }

    @Override
    public void failedConnect() {
        view.showReconnectButton();
    }

    public void reconnect() {
        interactor.reconnect();
    }

    public boolean checkConnection(){
        return interactor.checkConnection();
    }
}
