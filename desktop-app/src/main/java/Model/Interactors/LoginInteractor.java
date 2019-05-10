package Model.Interactors;

import Presenters.Callbacks.LoginCallback;

public interface LoginInteractor {
    void login(CharSequence login, CharSequence password);
    void checkUserData();
    void initLoginCallback(LoginCallback loginCallback);
}
