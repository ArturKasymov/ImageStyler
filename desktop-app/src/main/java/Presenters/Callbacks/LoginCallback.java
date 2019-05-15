package Presenters.Callbacks;

import java.util.ArrayList;

public interface LoginCallback {
    void showWrongDataAlert();
    void goToMain();
    ArrayList<Integer> getCachedImagesID(int userID);
}
