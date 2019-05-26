package Views.Interfaces;

public interface LoginView {
    void showWrongDataAlert();
    void goToRegister();
    void goToMain();
    void setAnimation(boolean set);
    void showReconnectButton();
}
