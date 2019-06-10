package Model.Database.Entity;

import Utils.annotations.Getter;

import static Utils.Constants.APP_ROOT_DIRECTORY;
import static Utils.Constants.separator;

public class User {
    private int id;
    private String userName;
    public User(int id, String userName){
        this.id=id;
        this.userName=userName;
    }

    @Getter
    public int getUserID() {
        return id;
    }

    @Getter
    public String getUserName() {
        return userName;
    }

    @Getter
    public String getCurrentUserPath(){
        return APP_ROOT_DIRECTORY+separator+"."+id;
    }
}
