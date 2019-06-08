package model;

import model.database.entity.User;
import model.database.provider.PostgreSQLDataProvider;
import model.repositories.CryptoRepo;
import model.repositories.JavaGeneration.BaseGeneration.VGG16.VGG16Generator;
import model.repositories.JavaGeneration.core.Generator;
import model.repositories.PythonGeneration.FastPyTransformer;
import model.repositories.RGBConverterRepo;
import model.repositories.PythonGeneration.PySqueezeNet;
import model.repositories.StyleRepo;
import util.Constants;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.sql.SQLException;

public class Interactor implements ClientInteractor {

    private static Interactor instance;
    private PostgreSQLDataProvider dataProvider;

    private Interactor(String dbname, String username,String password,String IP, int port){
        this.dataProvider=new PostgreSQLDataProvider(dbname,username,password,IP,port);
    }

    public static Interactor getInstance(){
        return instance;
    }

    public static Interactor createInstance(String dbname, String username,String password,String IP, int port){
        if(instance==null) instance= new Interactor(dbname,username,password,IP,port);
        return instance;
    }

    public void checkDataBase(){
        dataProvider.checkTables();
    }

    @Override
    public int insertUser(String userName, String password) throws SQLException {
        try {
            return dataProvider.insertUser(userName, CryptoRepo.getSaltedHash(password));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void deleteUserImage(int imageID, String userPath) {
        dataProvider.deleteUserImage(imageID);
        File file = new File(userPath + "/." + imageID + ".png");
        file.delete();
    }

    @Override
    public boolean changePassword(int userID, String oldPassword, String newPassword) {
        if (dataProvider.checkPassword(userID, oldPassword)) {
            dataProvider.changePassword(userID, newPassword);
            return true;
        }
        return false;
    }

    @Override
    public int insertImage(String imageName, int userID, long imageDate) {
        // TODO: CHANGE IMAGE STATUS AFTER
        return dataProvider.insertImage(imageName, userID, new Date(imageDate),false);
    }

    @Override
    public User getUser(String username) {
        return dataProvider.getUser(username);
    }

    @Override
    public BufferedImage generateImage(BufferedImage contentImage, int styleID, Constants.NEURAL_NET net, double d, boolean preserveSize) {
        BufferedImage styleImage = RGBConverterRepo.toBufferedImageOfType(StyleRepo.getStyle(styleID), 1);
        try {
            switch (net) {
                case SQUEEZENET:
                    return PySqueezeNet.generate(contentImage, styleImage, styleID, d, preserveSize);
                case VGG16:
                    Generator generator = new VGG16Generator(contentImage, styleImage, d);
                    return generator.generate();
                case TRANSFORMER:
                    return FastPyTransformer.generate(contentImage, styleID, d, preserveSize);
                default:
                    return PySqueezeNet.generate(contentImage, styleImage, styleID, d, preserveSize);
            }
        } catch (Exception e) {
            // TODO: handle
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void checkUserDir(String userPath) {
            File dir = new File(userPath);
            if (!dir.exists()) dir.mkdirs();
    }

    @Override
    public String getUserImagesListString(int userID) {
        return dataProvider.getUserImagesListString(userID);
    }

    public void initStyles(){

        StyleRepo.init();
    }

}
