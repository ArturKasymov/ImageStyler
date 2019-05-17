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

public class MainPresenter implements MainCallback {
    private MainViewImpl view;
    private MainInteractor interactor;

    public MainPresenter(MainViewImpl view) {
        this.view = view;
        this.interactor= Interactor.getInstance();
    }
    public void unsubscribe() { this.view = null; }

    public void logout(boolean local) {
        interactor.logout(local);
        Platform.runLater(()->view.goToLogin());
    }

    public void cleanCache() {}

    public void initUserData(){
        view.setUsernameLabel(interactor.getCurrentUserName());
    }

    public void changePassword(CharSequence oldPassword, CharSequence newPassword) {
        interactor.changeUserPassword(oldPassword, newPassword);
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
        Platform.runLater(()->view.notifyDownload(imageID));
    }

    public void getImageFromServer(int imageID) {
        interactor.getImageFromServer(imageID);
    }

    @Override
    public void showWrongDataAlert() {
        Platform.runLater(()->view.showChangeAlert());
    }

    @Override
    public void closeSettingsWindow() { Platform.runLater(()->view.closeSettingsWindow()); }
}
