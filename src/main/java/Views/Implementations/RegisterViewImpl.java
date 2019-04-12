package Views.Implementations;

import Views.BaseView;
import Views.Interfaces.RegisterView;
import Views.core.ViewByID;
import app.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class RegisterViewImpl extends BaseView implements RegisterView {
    //private RegisterPresenter presenter;
    private ViewManager viewManager;

    @Override
    public ViewByID getViewID() {
        return ViewByID.REGISTER_VIEW;
    }

    public RegisterViewImpl(){

    }
    /*public RegisterViewImpl(ViewManager viewManager){
        this.viewManager=viewManager;
    }*/

    @FXML
    public void initialize() {

    }
}
