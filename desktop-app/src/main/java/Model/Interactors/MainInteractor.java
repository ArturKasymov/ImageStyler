package Model.Interactors;

import Model.Database.Entity.UserImage;
import Presenters.Callbacks.MainCallback;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

public interface MainInteractor {
    String getCurrentUserName();
    boolean checkChangePassword(CharSequence oldPassword);
    void changeUserPassword(CharSequence newPassword);
    void logout();
    ArrayList<UserImage> getCurrentUserImagesList();
    void deleteImage(UserImage deletedImage);
    void initMainCallback(MainCallback callback);
    UserImage insertUserImage(int imageID, String name, Date date, boolean isWaiting);
    void saveUserImage(int imageID, BufferedImage image);

    void getImageFromServer(int imageID);
}
