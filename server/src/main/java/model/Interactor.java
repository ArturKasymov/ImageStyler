package model;

import model.database.entity.User;
import model.database.provider.PostgreSQLDataProvider;
import model.repositories.CryptoRepo;
import model.repositories.RGBConverterRepo;
import model.repositories.PythonGeneration.PySqueezeNet;
import model.repositories.StyleRepo;

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
        //TODO delete logs
        System.out.println(userName+" "+password);

        try {
            return dataProvider.insertUser(userName, CryptoRepo.getSaltedHash(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return 0;
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
    public BufferedImage generateImage(BufferedImage contentImage, int styleID) {
        BufferedImage styleImage = RGBConverterRepo.toBufferedImageOfType(StyleRepo.getStyle(styleID), 1);
        return PySqueezeNet.generate(contentImage, styleImage);
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
