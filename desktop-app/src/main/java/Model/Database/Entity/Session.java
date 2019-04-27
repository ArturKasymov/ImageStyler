package Model.Database.Entity;

import Utils.controls.Image;

import java.util.ArrayList;

public class Session {
    private long sessionId;
    private User currentUser;

    public Session(long sessionId,User currentUser){
        this.sessionId=sessionId;
        this.currentUser=currentUser;
    }

    public String getUserName(){
        return currentUser.getUserName();
    }

    public ArrayList<Image> getImages() { return null; }
}
