package Presenters;

import Model.Interactors.Interactor;
import Model.Interactors.MainInteractor;
import Views.Implementations.MainViewImpl;

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
    public void goToSettings() {}
    public void goToGenerator() {}
    public void cleanCache() {}
    public void initUserData(){
        view.setUsernameLabel(interactor.getCurrentUserName());
    };
}
