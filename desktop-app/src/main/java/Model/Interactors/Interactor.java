package Model.Interactors;

import Client.SessionManager;
import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import Model.Database.provider.SQLiteLocalDataProvider;;
import Presenters.Callbacks.GeneratorCallback;
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
import java.util.Optional;

import static Utils.Constants.APP_ROOT_DIRECTORY;
import static Utils.Constants.LOCAL_DATABASE_NAME;

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

    @Override
    public void initGeneratorCallback(GeneratorCallback callback) {
        sessionManager.initGeneratorCallback(callback);
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
    public void saveUserImage(int imageID, BufferedImage image) {
        File imageFile = new File(sessionManager.getCurrentUserPath()+"\\."+imageID+".png");
        OutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            ImageIO.write(image, "png", out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            sessionManager.getMainCallback().notifyDownload(imageID);
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
    public void deleteUserImage(int imageID) {
        sessionManager.deleteUserImage(imageID);
    }

    @Override
    public void getImageFromServer(int imageID) {
        sessionManager.getImage(imageID);
    }

    @Override
    public void generate(Image contentImage, int styleImageID, String imageName, Constants.NEURAL_NET net) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(contentImage, null);
        try {
            // TODO png<->jpg depending on the bufferedImage.getType()
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        } catch (IOException e) {
            //TODO Handle exception
            e.printStackTrace();
        }
        sessionManager.generateImage(byteArrayOutputStream, styleImageID, imageName, net);
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
        sessionManager.start();
    }
}
