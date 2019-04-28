package Model.Database.Entity;

import static Utils.Constants.APP_ROOT_DIRECTORY;

public class Session {
    private long sessionId;
    private User currentUser;
    private final String currentUserPath;

    public Session(long sessionId,User currentUser){
        this.sessionId=sessionId;
        this.currentUser=currentUser;
        this.currentUserPath=APP_ROOT_DIRECTORY+"\\."+currentUser.getId();
    }

    public String getUserName(){
        return currentUser.getUserName();
    }
    public int getUserId() {
        return currentUser.getId();
    }

    public String getCurrentUserPath(){
        return currentUserPath;
    }
    public String getCurrentUserPassword() { return currentUser.getPasswordHash(); }
}
