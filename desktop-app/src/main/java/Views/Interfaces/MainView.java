package Views.Interfaces;

import Model.Database.Entity.UserImage;
import Utils.Constants;

import java.util.ArrayList;

public interface MainView {
    void goToLogin();
    void setUsernameLabel(String s);
    void setResultImage(UserImage newUserImage);
    void notifyList(UserImage savedUserImage);

    Constants.NEURAL_NET getDefaultNeuralNet();

    ArrayList<UserImage> getUserImagesList();

    void getImageFromServer(int imageID);
}
