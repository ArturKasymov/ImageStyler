package Presenters;

import Views.Interfaces.RegisterView;

public class RegisterPresenter {

    private RegisterView view;

    public RegisterPresenter(RegisterView view){
        this.view = view;
    }

    public void register(CharSequence login, CharSequence password){
        view.goToMain();
    }


    public void unsubscribe(){
        this.view=null;
    }
}
