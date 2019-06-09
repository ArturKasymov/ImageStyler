package Model.Database.Entity;

import static Utils.Constants.APP_ROOT_DIRECTORY;
import static Utils.Constants.separator;

public class User {
    private int id;
    private String userName;
    public User(int id, String userName){
        this.id=id;
        this.userName=userName;
    }

    public int getUserID() {
        return id;
    }
    public String getUserName() {
        return userName;
    }
    public String getCurrentUserPath(){
        return APP_ROOT_DIRECTORY+separator+"."+id;
    }
}
