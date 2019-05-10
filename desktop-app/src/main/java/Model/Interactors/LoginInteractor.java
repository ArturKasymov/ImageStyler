package Model.Interactors;

import Presenters.Callbacks.LoginCallback;

public interface LoginInteractor {
    boolean checkLoginData(CharSequence login, CharSequence password);
    void checkUserData();
    void initLoginCallback(LoginCallback loginCallback);
}
