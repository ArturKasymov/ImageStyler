package Model.Interactors;

import Presenters.Callbacks.RegisterCallback;

public interface RegisterInteractor {
    void registerUser(CharSequence username, CharSequence password);
    void initRegisterCallback(RegisterCallback callback);
    void checkUserDirectory();
    void reconnect();
    boolean checkConnection();
}
