package Views;

import Presenters.Implementations.Presenter;
import Presenters.Interfaces.RegisterPresenter;
import app.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class RegisterView extends Parent {
    private RegisterPresenter presenter;
    private ViewManager app;

    public RegisterView() {
        this.app = new ViewManager();
        this.presenter = new Presenter(this);
    }

    @FXML
    public void initialize() {

    }
}
