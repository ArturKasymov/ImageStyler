package Model.Interactors;

public interface LoginInteractor {
    boolean checkLoginData(CharSequence login, CharSequence password);
    void checkUserData();
}
