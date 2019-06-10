package Presenters.Callbacks;

import Utils.annotations.Getter;

import java.util.ArrayList;
import java.util.Date;

public interface LoginCallback {
    void showWrongDataAlert();
    void goToMain();

    @Getter
    ArrayList<Integer> getCachedImagesID(int userID);

    void insertUserImage(int imageID, String name, Date date);
    void deleteImage(int imageID);
    void failedConnect();
}
