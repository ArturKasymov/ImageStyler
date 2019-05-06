package model.database.entity;

import java.util.Date;

public class Session {
    private final int id_session;
    private final int id_user;
    private Date last_update;

    public Session (int id_session,int id_user, Date last_update){
        this.id_session=id_session;
        this.id_user=id_user;
        this.last_update=last_update;
    }

    public int getSessionID(){
        return id_session;
    }
    public int getUserID(){
        return id_user;
    }
    public Date getLastUpdate(){
        return last_update;
    }
    public void setLastUpdate(Date date){
        this.last_update=date;
    }
}
