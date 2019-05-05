package Client;

import Model.Database.Entity.Session;
import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class SessionManager extends Thread {
    private Session currentSession;
    private boolean runningStatus;
    private Socket socket;

    private String serverIP;
    private int serverPort;


    public SessionManager(){
        this.runningStatus=true;
    }

    private boolean isContinue(){
        return runningStatus;
    }

    @Override
    public void run() {
        try {
            socket=new Socket(serverIP,serverPort);

            //TODO runtime logs
            System.out.println("server connect");

            while(isContinue()){
                System.out.println("I am running");


            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection(){
        this.runningStatus=false;
    }

    public void setSocketConfig(String serverIP, int serverPort){
        this.serverIP=serverIP;
        this.serverPort=serverPort;
    }

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
