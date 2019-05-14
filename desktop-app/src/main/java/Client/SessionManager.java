package Client;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import Presenters.Callbacks.GeneratorCallback;
import Presenters.Callbacks.LoginCallback;
import Presenters.Callbacks.MainCallback;
import Presenters.Callbacks.RegisterCallback;
import Utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static Utils.ServerCommand.*;

public class SessionManager extends Thread {
    private User currentUser;
    private boolean runningStatus;
    private Socket socket;
    private ScheduledExecutorService executor;


    private String serverIP;
    private int serverPort;

    private DataOutputStream outputStream;
    private DataInputStream dis;

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

    public SessionManager(){
        this.runningStatus=true;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    private boolean isContinue(){
        return runningStatus;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);
            dis = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            String inputData;

            //TODO delete logs
            System.out.println("server connect");

            while(isContinue()){
                inputData=dis.readUTF();
                System.out.println(inputData);
                if(inputData.equals(CLOSE_CONNECTION))break;
                parseServerInput(inputData);
            }
        } catch (IOException e) {
            //TODO go to offline
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            case INSERT_IMAGE:
                status = sc.next();
                switch (status){
                    case FAIL:
                        break;
                    case SUCCESS:
                        int imageID = sc.nextInt();
                        String imageName = sc.next();
                        long imageDate = sc.nextLong();
                        generatorCallback.insertGeneratedImage(imageID, imageName, new Date(imageDate));
                        break;
                }
                break;
            case INSERT_IMAGE_DATA:
                status = sc.next();
                switch (status){
                    case FAIL:
                        break;
                    case SUCCESS:
                        int imageID = sc.nextInt();
                        try {
                            byte[] imageSizeArray = new byte[4];
                            dis.read(imageSizeArray);
                            int size = ByteBuffer.wrap(imageSizeArray).asIntBuffer().get();
                            byte[] imageArray = new byte[size];
                            dis.readFully(imageArray,0,size);
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageArray));
                            executor.execute(()->generatorCallback.saveGeneratedImage(imageID, image));
                        } catch (Exception e){
                            e.printStackTrace();
                            //TODO handle
                        }
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
    public void logout() {
        synchronized (outputStream){
            try {
                outputStream.writeUTF(LOGOUT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currentUser=null;
    }
    public void register(String username, String password){
        synchronized (outputStream){
            try {
                outputStream.writeUTF(REGISTER + " " + username + " " + password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateImage(ByteArrayOutputStream userImage, int styleID, String imageName, Constants.NEURAL_NET net){
        synchronized (outputStream){
            try {
                outputStream.writeUTF(INSERT_IMAGE + " " + imageName + " " + styleID);

                byte[] userImageSize = ByteBuffer.allocate(4).putInt(userImage.size()).array();
                outputStream.write(userImageSize);
                outputStream.write(userImage.toByteArray());
                outputStream.flush();
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

}
