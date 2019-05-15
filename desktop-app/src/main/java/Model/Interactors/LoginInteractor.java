package Model.Interactors;

import Presenters.Callbacks.LoginCallback;

import java.util.ArrayList;

public interface LoginInteractor {
    void login(CharSequence login, CharSequence password);
    void checkUserData();
    void initLoginCallback(LoginCallback loginCallback);
    ArrayList<Integer> getUserCacheDImagesID(int userID);
}
