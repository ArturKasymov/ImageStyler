package Presenters;

import Model.Database.Entity.UserImage;
import Model.Interactors.Interactor;
import Model.Interactors.MainInteractor;
import Views.Implementations.MainViewImpl;

import java.util.ArrayList;

public class MainPresenter {
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
}
