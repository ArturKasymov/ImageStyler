package Model.Interactors;

import Client.SessionManager;
import Model.Database.Entity.UserImage;
import Model.Database.provider.SQLiteLocalDataProvider;;
import Presenters.Callbacks.LoginCallback;
import Presenters.Callbacks.MainCallback;
import Presenters.Callbacks.RegisterCallback;
import Utils.Constants;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Utils.Constants.*;

public class Interactor implements GeneratorInteractor, LoginInteractor, MainInteractor, RegisterInteractor {

    private static Interactor instance;

    private SQLiteLocalDataProvider dataProvider;
    private SessionManager sessionManager;
    private List<String> usersName;

    private Interactor(){
        this.dataProvider = new SQLiteLocalDataProvider(APP_ROOT_DIRECTORY+"\\"+LOCAL_DATABASE_NAME);
        dataProvider.checkTables();
        usersName = dataProvider.getLocalUsersNameList();
        sessionManager = new SessionManager();
    }

    public static Interactor getInstance(){
        if(instance == null) instance = new Interactor();
        return instance;
    }

    @Override
    public void login(CharSequence login, CharSequence password) {
        sessionManager.login(login.toString(),password.toString());
    }

    @Override
    public void registerUser(CharSequence username, CharSequence password) {
        sessionManager.register(username.toString(),password.toString());
    }

    @Override
    public boolean checkChangePassword(CharSequence oldPassword) {
        try {
            //return cryptoRepo.checkPassword(oldPassword.toString(), sessionManager.getCurrentUserPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void changeUserPassword(CharSequence oldPassword, CharSequence newPassword) {
        sessionManager.changeUserPassword(oldPassword.toString(), newPassword.toString());
    }

    @Override
    public void checkUserData() {
        checkUserDirectory();
    }

    @Override
    public void initLoginCallback(LoginCallback loginCallback) {
        sessionManager.initLoginCallback(loginCallback);
    }

    @Override
    public ArrayList<Integer> getUserCacheDImagesID(int userID) {
        return dataProvider.getUserImagesID(userID);
    }

    @Override
    public void initRegisterCallback(RegisterCallback callback) {
        sessionManager.initRegisterCallback(callback);
    }

    @Override
    public void initMainCallback(MainCallback callback) {
        sessionManager.initMainCallback(callback);
    }

    public void checkUserDirectory(){
        File dir = new File(sessionManager.getCurrentUserPath());
        if (!dir.exists()) dir.mkdirs();
    }

    @Override
    public UserImage insertUserImage(int imageID, String name, Date date, boolean isWaiting) {
        dataProvider.insertUserImage(imageID, name, sessionManager.getCurrentUserId(), date,false);
        return new UserImage(imageID, name, sessionManager.getCurrentUserId(), date,false,isWaiting);
    }

    @Override
    public void saveUserImage(int imageID, int userID,BufferedImage image) {
        final String path=APP_ROOT_DIRECTORY+separator+"."+userID+separator+"."+imageID+".png";
        File imageFile = new File(path);
        OutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            ImageIO.write(image, "png", out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            dataProvider.updateUserImageIsDownloaded(imageID);
        }
    }

    @Override
    public void deleteLocalImage(int imageID) {
        dataProvider.deleteUserImage(imageID);
        File file = new File(sessionManager.getCurrentUserPath()+"\\."+imageID+".png");
        file.delete();
    }

    @Override
    public boolean checkConnection() {
        return sessionManager.isContinue();
    }

    @Override
    public void deleteUserImage(int imageID) {
        sessionManager.deleteUserImage(imageID);
    }

    @Override
    public void getImageFromServer(int imageID) {
        sessionManager.getImage(imageID);
    }

    @Override
    public void cleanCache() {
        int userID=sessionManager.getCurrentUserId();
        if(userID!=0){
            List<Integer> downloadedImages=dataProvider.getDownloadedImagesID(userID);
            for (int temp: downloadedImages){
                try {
                    File file = new File(APP_ROOT_DIRECTORY+separator+"."+userID+separator+"."+temp+".png");
                    file.delete();
                } catch (Exception e){}
            }
        }

    }

    @Override
    public void generate(Image contentImage, int styleImageID, String imageName, Constants.NEURAL_NET net, double strength, boolean preserveSize) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(contentImage, null);
        try {
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sessionManager.generateImage(byteArrayOutputStream, styleImageID, imageName, net, strength, preserveSize);
    }


    @Override
    public String getCurrentUserName() {
        return sessionManager.getCurrentUserName();
    }

    @Override
    public void logout(boolean local) {
        sessionManager.logout(local);
    }

    @Override
    public ArrayList<UserImage> getCurrentUserImagesList() {
        return dataProvider.getUserImages(sessionManager.getCurrentUserId());
    }

    public void stopSessionManager() {
        sessionManager.stopConnection();
    }

    public void startSessionManager(String serverIP, int serverPort){
        sessionManager.setSocketConfig(serverIP,serverPort);
        new Thread(sessionManager).start();
    }

    @Override
    public void reconnect(){
        new Thread(sessionManager).start();
    }
}
