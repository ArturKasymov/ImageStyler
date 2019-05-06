package Model.Interactors;

public interface RegisterInteractor {
    boolean checkUserExists(CharSequence username);
    boolean insertUser(CharSequence username, CharSequence password);
}
