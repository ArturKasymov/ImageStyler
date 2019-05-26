package Model.Interactors;

import Model.Database.Entity.UserImage;
import Presenters.Callbacks.MainCallback;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

public interface MainInteractor {
    String getCurrentUserName();
    boolean checkChangePassword(CharSequence oldPassword);
    void changeUserPassword(CharSequence oldPassword, CharSequence newPassword);
    void logout(boolean local);
    ArrayList<UserImage> getCurrentUserImagesList();
    void initMainCallback(MainCallback callback);
    UserImage insertUserImage(int imageID, String name, Date date, boolean isWaiting);
    void saveUserImage(int imageID, int userID,BufferedImage image);
    void deleteUserImage(int imageID);
    void deleteLocalImage(int imageID);

    void getImageFromServer(int imageID);
    void cleanCache();
}
