package Views.Interfaces;

import Utils.controls.Image;

public interface MainView {
    void goToLogin();
    void setUsernameLabel(String s);
    void setResultImage(Image newImage);
    void notifyList(Image savedImage);
}
