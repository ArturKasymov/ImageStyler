package model;

import model.database.provider.PostgreSQLDataProvider;
import model.repositories.cryptoRepo;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Date;


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
            return dataProvider.insertUser(userName, cryptoRepo.getSaltedHash(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int insertSession(int userID, Date lastUpdate) {
        try {
            return dataProvider.insertSession(userID, lastUpdate);
        } catch (Exception e) {
                e.printStackTrace();
        }
        return 0;
    }
}
