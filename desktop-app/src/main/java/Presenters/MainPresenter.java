package Presenters;

import Model.Database.Entity.UserImage;
import Model.Interactors.Interactor;
import Model.Interactors.MainInteractor;
import Presenters.Callbacks.MainCallback;
import Views.Implementations.MainViewImpl;
import javafx.application.Platform;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class MainPresenter implements MainCallback {
    private MainViewImpl view;
    private MainInteractor interactor;

    public MainPresenter(MainViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }
    public void unsubscribe() { this.view = null; }
    public void logout() {
        interactor.logout();
        view.goToLogin();
    }
    public void cleanCache() {}
    public void initUserData(){
        view.setUsernameLabel(interactor.getCurrentUserName());

    }

    public void changePassword(CharSequence oldPassword, CharSequence newPassword) {
        if (interactor.checkChangePassword(oldPassword)) {
            interactor.changeUserPassword(newPassword);
        } else {
            view.showChangeAlert();
        }
    }

    public ArrayList<UserImage> getUserImagesList() {
        return interactor.getCurrentUserImagesList();
    }

    public void deleteLocalImage(int imageID) {
        interactor.deleteLocalImage(imageID);
    }

    public void deleteUserImage(int imageID) {
        interactor.deleteUserImage(imageID);
    }

    public void initCallback(){
        interactor.initMainCallback(this);
    }

    @Override
    public void insertGeneratedImage(int imageID, String photoName, Date date) {
        Platform.runLater(()->view.notifyList(interactor.insertUserImage(imageID, photoName, date,true)));
    }

    @Override
    public void saveGeneratedImage(int imageID, BufferedImage generatedImage) {
        interactor.saveUserImage(imageID,generatedImage);
    }

    @Override
    public void notifyDownload(int imageID) {
        view.notifyDownload(imageID);
    }

    public void getImageFromServer(int imageID) {
        interactor.getImageFromServer(imageID);
    }
}
