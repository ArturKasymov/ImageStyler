package Presenters;

import Views.Implementations.MainViewImpl;

public class MainPresenter {
    private MainViewImpl view;
    public MainPresenter(MainViewImpl view) { this.view = view; }
    public void unsubscribe() { this.view = null; }
    public void logout() {
        view.goToLogin();
    }
    public void showSettings() {}
    public void goToGenerator() {}
}
