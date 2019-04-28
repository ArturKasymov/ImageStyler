package Model.Interactors;

import Model.Database.Entity.UserImage;

import java.util.ArrayList;

public interface MainInteractor {
    String getCurrentUserName();
    void logout();
    ArrayList<UserImage> getCurrentUserImagesList();
}
