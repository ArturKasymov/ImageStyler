package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.LoginInteractor;
import Presenters.Callbacks.LoginCallback;
import Views.Implementations.LoginViewImpl;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Date;


public class LoginPresenter implements LoginCallback{
    private LoginViewImpl view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginViewImpl view) {
        this.view=view;
        this.interactor=Interactor.getInstance();
    }

    public void login(CharSequence login, CharSequence password) {
        interactor.login(login, password);
        //ToDo add animation while getting data from server
    }

    public void initCallback(){
        interactor.initLoginCallback(this);
    }

    public void register() {
        view.goToRegister();
    }

    public void showWrongDataAlert(){
        Platform.runLater(()->view.showWrongDataAlert());
    }
    public void goToMain(){
        interactor.checkUserData();
        view.goToMain();
    }

    @Override
    public ArrayList<Integer> getCachedImagesID(int userID) {
        return interactor.getUserCacheDImagesID(userID);
    }

    @Override
    public void insertUserImage(int imageID, String name, Date date) {
        interactor.insertUserImage(imageID,name,date,false);
    }

    @Override
    public void deleteImage(int imageID) {
        interactor.deleteLocalImage(imageID);
    }
}
