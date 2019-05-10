package model.database.entity;

public class User {
    private final int id_user;
    private String user_name;
    private String password_hash;

    public User(int id_user, String user_name, String password_hash) {
        this.id_user = id_user;
        this.user_name = user_name;
        this.password_hash = password_hash;
    }

    public int getId_user() {
        return id_user;
    }
    public String getUser_name() {
        return user_name;
    }
    public String getPassword_hash() {
        return password_hash;
    }
}
