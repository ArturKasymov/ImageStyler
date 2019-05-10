package Client;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import Presenters.Callbacks.GeneratorCallback;
import Presenters.Callbacks.LoginCallback;
import Presenters.Callbacks.MainCallback;
import Presenters.Callbacks.RegisterCallback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import static Utils.ServerCommand.*;

public class SessionManager extends Thread {
    private User currentUser;
    private boolean runningStatus;
    private Socket socket;

    private String serverIP;
    private int serverPort;

    private DataOutputStream outputStream;

    //CallBacks
    private LoginCallback loginCallback;
    private GeneratorCallback generatorCallback;
    private MainCallback mainCallback;
    private RegisterCallback registerCallback;


    public void initLoginCallback(LoginCallback callback){
        loginCallback=callback;
    }
    public void initGeneratorCallback(GeneratorCallback callback){
        generatorCallback=callback;
    }
    public void initMainCallback(MainCallback callback){
        mainCallback=callback;
    }
    public void initRegisterCallback(RegisterCallback callback){
        registerCallback=callback;
    }


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
            outputStream=new DataOutputStream(socket.getOutputStream());
            String inputData;

            //TODO delete logs
            System.out.println("server connect");

            while(isContinue()){
                inputData=dis.readUTF();
                System.out.println(inputData);
                if(inputData.equals(CLOSE_CONNECTION))break;
                parseServerInput(inputData);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseServerInput(String input){
        Scanner sc = new Scanner(input);
        String command=sc.next();
        String status;
        switch (command){
            case LOGIN:
                status=sc.next();
                switch (status){
                    case FAIL:
                        loginCallback.showWrongDataAlert();
                        break;
                    case SUCCESS:
                        int userID=sc.nextInt();
                        String userName=sc.next();
                        currentUser=new User(userID,userName);
                        loginCallback.goToMain();
                        break;
                }
                break;
            case REGISTER:
                status=sc.next();
                switch (status){
                    case FAIL:
                        registerCallback.showAlert();
                        break;
                    case SUCCESS:
                        int userID=sc.nextInt();
                        String userName=sc.next();
                        currentUser=new User(userID,userName);
                        registerCallback.goToMain();
                        break;
                }
                break;
        }
    }

    public void login(String username, String password) {
        synchronized (outputStream){
            try {
                outputStream.writeUTF(LOGIN+" "+username+" "+password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(String username, String password){
        synchronized (outputStream){
            try {
                outputStream.writeUTF(REGISTER+" "+username+" "+password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection(){
        this.runningStatus=false;
        synchronized (outputStream){
            try {
                outputStream.writeUTF(CLOSE_CONNECTION);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSocketConfig(String serverIP, int serverPort){
        this.serverIP=serverIP;
        this.serverPort=serverPort;
    }

    public String getCurrentUserName(){
        return currentUser.getUserName();
    }

    public int getCurrentUserId(){
        return currentUser.getUserID();
    }

    public ArrayList<UserImage> checkCurrentUserImages() {
        //TODO get Images from server
        return null;
    }

    public String getCurrentUserPath(){
        return currentUser.getCurrentUserPath();
    }


    public void logout() {
        currentUser=null;
    }
}
