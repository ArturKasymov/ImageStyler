package Model.Interactors;

import Model.Database.Entity.UserImage;
import Presenters.Callbacks.MainCallback;
import Utils.annotations.Getter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

public interface MainInteractor {
    @Getter
    String getCurrentUserName();

    void changeUserPassword(CharSequence oldPassword, CharSequence newPassword);
    void logout(boolean local);

    @Getter
    ArrayList<UserImage> getCurrentUserImagesList();

    void initMainCallback(MainCallback callback);
    UserImage insertUserImage(int imageID, String name, Date date, boolean isWaiting);
    void saveUserImage(int imageID, int userID,BufferedImage image);
    void deleteUserImage(int imageID);
    void deleteLocalImage(int imageID);

    @Getter
    void getImageFromServer(int imageID);

    void cleanCache();
}
