package Client;

import Model.Database.Entity.Session;
import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;

import java.util.ArrayList;

public class SessionManager {
    private Session currentSession;


    public String getCurrentUserName(){
        return currentSession.getUserName();
    }

    public int getCurrentUserId(){
        return currentSession.getUserId();
    }

    public String getCurrentUserPassword() { return currentSession.getCurrentUserPassword(); }

    public ArrayList<UserImage> checkCurrentUserImages() {
        //TODO get Images from server
        return null;
    }

    public String getCurrentUserPath(){
        return currentSession.getCurrentUserPath();
    }

    public void startSession(long sessionId, User user){
        this.currentSession=new Session(sessionId,user);
    }

    public void finishSession(){
        this.currentSession=null;
    }
}
