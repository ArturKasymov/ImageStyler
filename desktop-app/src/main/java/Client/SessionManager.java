package Client;

import Model.Database.Entity.Session;
import Model.Database.Entity.User;

public class SessionManager {
    private Session currentSession;


    public String getCurrentUserName(){
        return currentSession.getUserName();
    }

    public void startSession(long sessionId, User user){
        this.currentSession=new Session(sessionId,user);
    }

    public void finishSession(){
        this.currentSession=null;
    }
}
