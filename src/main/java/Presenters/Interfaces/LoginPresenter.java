package Presenters.Interfaces;

public interface LoginPresenter {
    void login(CharSequence login, CharSequence password);
    void setView(String s);
}
