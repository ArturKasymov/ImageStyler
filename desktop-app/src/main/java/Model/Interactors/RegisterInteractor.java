package Model.Interactors;

public interface RegisterInteractor {
    boolean checkUserExists(CharSequence username);
    void insertUser(CharSequence username, CharSequence password);
}
