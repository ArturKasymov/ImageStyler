package Client;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import Presenters.Callbacks.GeneratorCallback;
import Presenters.Callbacks.LoginCallback;
import Presenters.Callbacks.MainCallback;
import Presenters.Callbacks.RegisterCallback;
import Utils.Constants;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static Utils.ServerCommand.*;

public class SessionManager implements Runnable {
    private final User defaultUser=new User(0,"ghost");


    private User currentUser=defaultUser;

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

    }

    public boolean isContinue(){
        return runningStatus;
    }



    @Override
    public void run() {
        try {
            executor = Executors.newSingleThreadScheduledExecutor();
            socket = new Socket(serverIP,serverPort);
            dis = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            this.runningStatus=true;
            String inputData;

            //TODO delete logs
            System.out.println("server connected");

            while(isContinue()){
                inputData=dis.readUTF();
                System.out.println(inputData);
                if(inputData.equals(CLOSE_CONNECTION))break;
                parseServerInput(inputData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            runningStatus=false;
            outputStream=null;
            Platform.runLater(()->{
                if(currentUser!=defaultUser)mainCallback.logout(true);
                loginCallback.failedConnect();
                registerCallback.failedConnect();
            });
        } finally {
            try {
                socket.close();
                executor.shutdown();

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

                        int userImagesCount=sc.nextInt();
                        if(userImagesCount>0){
                            ArrayList<Integer> cachedImages=loginCallback.getCachedImagesID(userID);
                            int currentCheckIndex=0;

                            int imageID;
                            String imageName;
                            long imageDate;
                            for(int i=0;i<userImagesCount;i++) {
                                imageID = sc.nextInt();
                                imageName = sc.next();
                                imageDate = sc.nextLong();
                                if (currentCheckIndex < cachedImages.size()) {
                                    if (imageID == cachedImages.get(currentCheckIndex)) {

                                        currentCheckIndex++;
                                        continue;
                                    } else loginCallback.deleteImage(cachedImages.get(currentCheckIndex++));
                                }
                                loginCallback.insertUserImage(imageID,imageName,new Date(imageDate));
                            }
                        }
                        loginCallback.goToMain();
                        break;
                }
                break;
            case LOGOUT:
                mainCallback.logout(true);
                break;
            case REGISTER:
                status=sc.next();
                switch (status) {
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
                        int commandUserID=sc.nextInt();
                        String imageName = sc.next();
                        long imageDate = sc.nextLong();
                        if(currentUser.getUserID()==commandUserID)mainCallback.insertGeneratedImage(imageID,imageName, new Date(imageDate));
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
                        int userID= sc.nextInt();
                        try {
                            byte[] imageSizeArray = new byte[4];
                            dis.read(imageSizeArray);
                            int size = ByteBuffer.wrap(imageSizeArray).asIntBuffer().get();
                            byte[] imageArray = new byte[size];
                            dis.readFully(imageArray,0,size);
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageArray));
                            if(currentUser.getUserID()==userID)executor.execute(()->mainCallback.saveGeneratedImage(imageID, userID,image));
                        } catch (Exception e){
                            e.printStackTrace();
                            //TODO handle
                        }
                        break;
                }
                break;
            case DELETE_IMAGE:
                status = sc.next();
                switch (status) {
                    case FAIL:
                        break;
                    case SUCCESS:
                        int imageID = sc.nextInt();
                        int userID= sc.nextInt();
                        executor.execute(()->mainCallback.deleteLocalImage(imageID,userID));
                        break;
                }
                break;
            case CHANGE_PASSWORD:
                status = sc.next();
                switch (status) {
                    case FAIL:
                        mainCallback.showWrongDataAlert();
                        break;
                    case SUCCESS:
                        mainCallback.closeSettingsWindow();
                        break;
                }
                break;
        }
    }

    public void login(String username, String password) {
        sendDataToServer(LOGIN+" "+username+" "+password);
    }
    public void logout(boolean local) {
        if (!local) sendDataToServer(LOGOUT);
        currentUser=defaultUser;
    }
    public void register(String username, String password){
        sendDataToServer(REGISTER + " " + username + " " + password);
    }

    public void generateImage(ByteArrayOutputStream userImage, int styleID, String imageName, Constants.NEURAL_NET net, double strength, boolean preserveSize) {
        synchronized (outputStream){
            try {
                outputStream.writeUTF(INSERT_IMAGE + " " + imageName + " " + styleID + " " + net + " " + strength + " " + preserveSize);
                byte[] userImageSize = ByteBuffer.allocate(4).putInt(userImage.size()).array();
                outputStream.write(userImageSize);
                outputStream.write(userImage.toByteArray());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeUserPassword(String oldPassword, String newPassword) {
        sendDataToServer(CHANGE_PASSWORD + " " + oldPassword + " " + newPassword);
    }

    public void deleteUserImage(int imageID) {
        sendDataToServer(DELETE_IMAGE + " " + imageID);
    }

    public void getImage(int imageID) {
        sendDataToServer(GET_IMAGE+" "+imageID);
    }

    public void stopConnection(){
        if(runningStatus){
            runningStatus=false;
            synchronized (outputStream){
                try {
                    outputStream.writeUTF(CLOSE_CONNECTION);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public String getCurrentUserPath(){
        return currentUser.getCurrentUserPath();
    }


    private void sendDataToServer(String data){
        //TODO add hashing
        synchronized (outputStream){
            try {
                outputStream.writeUTF(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
