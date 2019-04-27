package Model.Interactors;

import Utils.controls.Image;

import java.util.ArrayList;

public interface MainInteractor {
    String getCurrentUserName();
    ArrayList<Image> getCurrentUserImages();
    void logout();
}
