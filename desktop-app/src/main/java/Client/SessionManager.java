package Client;

import Model.Database.Entity.Session;
import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import static Utils.ServerCommand.*;

public class SessionManager extends Thread {
    private Session currentSession;
    private boolean runningStatus;
    private Socket socket;

    private String serverIP;
    private int serverPort;

    //TODO rewrite
    private ArrayBlockingQueue<String> commandsToServer;
    private ArrayBlockingQueue<String> commandsResults;

    public SessionManager(){
        this.runningStatus=true;
        commandsToServer=new ArrayBlockingQueue<>(3);
        commandsResults=new ArrayBlockingQueue<>(3);
    }

    private boolean isContinue(){
        return runningStatus;
    }

    @Override
    public void run() {
        try {
            socket=new Socket(serverIP,serverPort);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //TODO delete logs
            System.out.println("server connect");
            String result;
            String command;
            while(isContinue()){
                result=dis.readUTF();
                System.out.println(result);
                if(!result.equals(WAITING_COMMANDS)){
                    commandsResults.put(result);
                    continue;
                }
                command=commandsToServer.take();
                System.out.println(command);
                dos.writeUTF(command);
            }
            dos.writeUTF(CLOSE_CONNECTION);
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean insertUser(String login, String password){

        try {
            System.out.println(String.format("%s %s %s",REGISTER_USER,login,password));
            commandsToServer.put(String.format("%s %s %s",REGISTER_USER,login,password));
            System.out.println("command in Queue");
            String result=commandsResults.take();
            if(result.equals(REGISTER_USER_EXCEPTION))return false;
            System.out.println(result);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
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
