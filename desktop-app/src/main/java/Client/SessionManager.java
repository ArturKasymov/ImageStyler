package Client;

import Model.Database.Entity.Session;
import Model.Database.Entity.User;
import Utils.controls.Image;

import java.util.ArrayList;

public class SessionManager {
    private Session currentSession;


    public String getCurrentUserName(){
        return currentSession.getUserName();
    }

    public ArrayList<Image> getCurrentUserImages() { return currentSession.getImages(); }

    public void startSession(long sessionId, User user){
        this.currentSession=new Session(sessionId,user);
    }

    public void finishSession(){
        this.currentSession=null;
    }
}
