package Presenters.Callbacks;

import Model.Database.Entity.UserImage;

import java.util.ArrayList;
import java.util.Date;

public interface LoginCallback {
    void showWrongDataAlert();
    void goToMain();
    ArrayList<Integer> getCachedImagesID(int userID);
    void insertUserImage(int imageID, String name, Date date);
    void deleteImage(int imageID);
}
