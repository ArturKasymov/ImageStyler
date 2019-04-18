package Model.Database.Entity;

public class User {
    private int id;
    private String userName;
    private String passwordHash;
    public User(int id, String userName, String passwordHash){
        this.id=id;
        this.userName=userName;
        this.passwordHash=passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
