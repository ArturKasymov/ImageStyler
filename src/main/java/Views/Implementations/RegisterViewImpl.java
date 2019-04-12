package Views.Implementations;

import Presenters.RegisterPresenter;
import Views.BaseView;
import Views.Interfaces.RegisterView;
import Views.core.ViewByID;
import javafx.fxml.FXML;


public class RegisterViewImpl extends BaseView implements RegisterView {
    private RegisterPresenter presenter;

    @Override
    public ViewByID getViewID() {
        return ViewByID.REGISTER_VIEW;
    }

    public RegisterViewImpl(){
        presenter= new RegisterPresenter(this);
    }


    public void goToMain(){
        presenter.unsubscribe();
        changeViewTo(new MainViewImpl());
    }

    @FXML
    public void initialize() {

    }
}
