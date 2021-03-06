package Client;

import Model.Database.Entity.User;
import Model.Repositories.ConnectionCryptoRepo;
import Presenters.Callbacks.LoginCallback;
import Presenters.Callbacks.MainCallback;
import Presenters.Callbacks.RegisterCallback;
import Utils.Constants;
import Utils.annotations.Getter;
import Utils.annotations.Setter;
import javafx.application.Platform;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static Utils.Constants.KEY_GEN_LENGTH;
import static Utils.ServerCommand.*;

public class SessionManager implements Runnable {
    private final User defaultUser = new User(0,"ghost");
    private User currentUser = defaultUser;

    private boolean runningStatus;

    private Socket socket;
    private ScheduledExecutorService executor;

    private String serverIP;
    private int serverPort;

    private DataOutputStream outputStream;
    private DataInputStream dis;

    private ConnectionCryptoRepo connectionCryptoRepo;

    //CallBacks
    private LoginCallback loginCallback;
    private MainCallback mainCallback;
    private RegisterCallback registerCallback;

    public void initLoginCallback(LoginCallback callback){
        loginCallback=callback;
    }
    public void initMainCallback(MainCallback callback){
        mainCallback=callback;
    }
    public void initRegisterCallback(RegisterCallback callback){
        registerCallback=callback;
    }

    public SessionManager(){}

    @Override
    public void run() {
        try {
            executor = Executors.newSingleThreadScheduledExecutor();

            try {
                connectionCryptoRepo=new ConnectionCryptoRepo(KEY_GEN_LENGTH);
            }catch (Exception e){
                e.printStackTrace();
            }

            /**
             * SET UP SERVER COMMUNICATION
             */
            socket = new Socket(serverIP,serverPort);
            dis = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            this.runningStatus=true;
            String inputData;

            //TODO delete logs
            System.out.println("server connected");
            connectionCryptoRepo.sendPublicKey(outputStream);

            /**
             * WAIT FOR NOTIFICATIONS FROM SERVER
             */
            while(isContinue()){
                inputData=dis.readUTF();
                if(inputData.equals(INIT)){
                    connectionCryptoRepo.setServerPublicKey(dis);
                    System.out.println("Connection INIT");
                    continue;
                }
                inputData=connectionCryptoRepo.decryptMessage(inputData);
                if(inputData.equals(CLOSE_CONNECTION))break;
                parseServerInput(inputData);
            }
        } catch (Exception e) {
            /**
             * CONNECTION FAILED
             */
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
                        int cryptBytesSize=sc.nextInt();
                        byte[] msg=null;
                        try {
                            msg=connectionCryptoRepo.decryptBytes(dis,cryptBytesSize);
                        } catch (BadPaddingException | IllegalBlockSizeException | IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        }
                        Scanner usc = new Scanner(new String(msg));

                        int userImagesCount=usc.nextInt();
                        if(userImagesCount>0){
                            ArrayList<Integer> cachedImages=loginCallback.getCachedImagesID(userID);
                            int currentCheckIndex=0;

                            int imageID;
                            String imageName;
                            long imageDate;
                            for(int i=0;i<userImagesCount;i++) {
                                imageID = usc.nextInt();
                                imageName = usc.next();
                                imageDate = usc.nextLong();
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
                        int encryptImageSize= sc.nextInt();
                        try {
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(connectionCryptoRepo.decryptBytes(dis,encryptImageSize)));
                            if(currentUser.getUserID()==userID)executor.execute(()->mainCallback.saveGeneratedImage(imageID, userID,image));
                        } catch (Exception e){
                            e.printStackTrace();
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
            case AES:
                int size= sc.nextInt();
                connectionCryptoRepo.setAES(dis,size);
                System.out.println("AES init");
                break;
        }
    }

    public boolean isContinue(){
        return runningStatus;
    }

    /**
     * SEND LOGIN DATA TO SERVER
     * @param username entered by user
     * @param password entered by user
     */

    public void login(String username, String password) {
        sendDataToServer(LOGIN+" "+username+" "+password);
    }

    /**
     * SEND LOGOUT NOTIFICATION TO SERVER and logout from currentUser
     * @param local whether to logout from local session or from server too
     */

    public void logout(boolean local) {
        if (!local) sendDataToServer(LOGOUT);
        currentUser=defaultUser;
    }

    /**
     * SEND REGISTER DATA TO SERVER
     * @param username entered by user
     * @param password entered by user
     */

    public void register(String username, String password){
        sendDataToServer(REGISTER + " " + username + " " + password);
    }

    /**
     * SEND IMAGE-GENERATION DATA TO SERVER
     * @param userImage image byte-array
     * @param styleID ID of the chosen style image
     * @param imageName entered by user
     * @param net chosen Neural Net
     * @param strength of the style impact
     * @param preserveSize whether to preserve original size of the content image
     */

    public void generateImage(ByteArrayOutputStream userImage, int styleID, String imageName, Constants.NEURAL_NET net, double strength, boolean preserveSize) {
        synchronized (outputStream){
            try {
                byte [] encryptImage= connectionCryptoRepo.encryptImage(userImage.toByteArray());
                outputStream.writeUTF(connectionCryptoRepo.encryptMessage
                        (INSERT_IMAGE + " " + imageName + " " + styleID + " " + net + " "
                                + strength + " " + preserveSize + " " + encryptImage.length));
                outputStream.write(encryptImage);
                outputStream.flush();
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SEND CHANGED USER DATA TO SERVER
     * @param oldPassword entered by user (and to be checked)
     * @param newPassword entered by user
     */

    public void changeUserPassword(String oldPassword, String newPassword) {
        sendDataToServer(CHANGE_PASSWORD + " " + oldPassword + " " + newPassword);
    }

    /**
     * ASK SERVER TO DELETE GIVEN IMAGE
     * @param imageID of the image
     */

    public void deleteUserImage(int imageID) {
        sendDataToServer(DELETE_IMAGE + " " + imageID);
    }

    /**
     * ASK SERVER FOR AN IMAGE WITH
     * @param imageID of the image
     */

    public void getImage(int imageID) {
        sendDataToServer(GET_IMAGE+" "+imageID);
    }

    /**
     * STOP CONNECTION WITH SERVER
     */

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

    @Setter
    public void setSocketConfig(String serverIP, int serverPort){
        this.serverIP=serverIP;
        this.serverPort=serverPort;
    }

    @Getter
    public String getCurrentUserName(){
        return currentUser.getUserName();
    }

    @Getter
    public int getCurrentUserId(){
        return currentUser.getUserID();
    }

    @Getter
    public String getCurrentUserPath(){
        return currentUser.getCurrentUserPath();
    }

    /**
     * SEND STRING DATA TO SERVER
     * @param data to be sent
     */

    private void sendDataToServer(String data){
        synchronized (outputStream){
            try {
                outputStream.writeUTF(connectionCryptoRepo.encryptMessage(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
