package Model.Interactors;

import Model.Database.Entity.UserImage;

import java.util.ArrayList;

public interface MainInteractor {
    String getCurrentUserName();
    boolean checkChangePassword(CharSequence oldPassword);
    void changeUserPassword(CharSequence newPassword);
    void logout();
    ArrayList<UserImage> getCurrentUserImagesList();
}
