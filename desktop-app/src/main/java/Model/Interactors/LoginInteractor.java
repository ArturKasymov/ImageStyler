package Model.Interactors;

import Model.Database.Entity.UserImage;
import Presenters.Callbacks.LoginCallback;
import Utils.annotations.Getter;

import java.util.ArrayList;
import java.util.Date;

public interface LoginInteractor {
    void login(CharSequence login, CharSequence password);
    void checkUserData();
    void initLoginCallback(LoginCallback loginCallback);

    @Getter
    ArrayList<Integer> getUserCachedImagesID(int userID);

    UserImage insertUserImage(int imageID, String name, Date date, boolean isWaiting);
    void deleteLocalImage(int imageID);
    void reconnect();
    boolean checkConnection();
}
