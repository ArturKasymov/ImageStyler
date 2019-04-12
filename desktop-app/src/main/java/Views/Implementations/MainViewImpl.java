package Views.Implementations;

import Presenters.MainPresenter;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import Views.core.ViewByID;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainViewImpl extends BaseView implements MainView {
    private MainPresenter presenter;
    public MainViewImpl() { this.presenter = new MainPresenter(this); }

    @Override
    public ViewByID getViewID() {
        return ViewByID.MAIN_VIEW;
    }

    @FXML
    private Button logOutButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button generateButton;

    @FXML
    public void initialize() {

    }

    @FXML
    protected void onLogOut(ActionEvent e) {
        presenter.logout();
    }

    @FXML
    protected void onSettings(ActionEvent e) {
        presenter.showSettings();
    }

    @FXML
    protected void onGenerate() {
        presenter.goToGenerator();
    }

    public void goToLogin() {
        //presenter.unsubscribe();
        changeViewTo(new LoginViewImpl());
    }

}
