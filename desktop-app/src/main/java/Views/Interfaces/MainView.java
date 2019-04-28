package Views.Interfaces;

import Model.Database.Entity.UserImage;

import java.util.ArrayList;

public interface MainView {
    void goToLogin();
    void setUsernameLabel(String s);
    void setResultImage(UserImage newUserImage);
    void notifyList(UserImage savedUserImage);
    ArrayList<UserImage> getUserImagesList();
}
