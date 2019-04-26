package Model.Interactors;

import Model.Database.Entity.User;
import Model.Database.provider.SQLiteLocalDataProvider;
import Model.Repositories.ImageRepo;
import Model.Repositories.cryptoRepo;
import javafx.scene.image.Image;


import java.util.Date;
import java.util.List;
import java.util.Random;

import static Utils.Constants.APP_ROOT_DIRECTORY;
import static Utils.Constants.LOCAL_DATABASE_NAME;

public class Interactor implements GeneratorInteractor, LoginInteractor, MainInteractor, RegisterInteractor {

    private static Interactor instance;

    private SQLiteLocalDataProvider dataProvider;
    private List<String> usersName;

    private Interactor(){
        this.dataProvider=new SQLiteLocalDataProvider(APP_ROOT_DIRECTORY+"\\"+LOCAL_DATABASE_NAME);
        dataProvider.checkTables();
        usersName = dataProvider.getLocalUsersNameList();
    }

    public static Interactor getInstance(){
        if(instance==null) instance=new Interactor();
        return instance;
    }

    @Override
    public boolean checkUserData(CharSequence login, CharSequence password) {
        try {
            String userLogin = login.toString();
            String userPassword = password.toString();
            User storedUser = dataProvider.getUser(userLogin);
            return cryptoRepo.checkPassword(userPassword, storedUser.getPasswordHash());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkUserExists(CharSequence login) {
        String userLogin = login.toString();
        return usersName.contains(userLogin);
    }

    @Override
    public void insertUser(CharSequence login, CharSequence password) {
        try {
            dataProvider.insertUser(login.toString(), cryptoRepo.getSaltedHash(password.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertGeneratedImage(Image image, String name, Date date) {
        dataProvider.insertGeneratedImage(image, name, date);
    }

    @Override
    public Image generate(Image contentImage, Image styleImage) {
        ImageRepo generator = new ImageRepo(contentImage, styleImage);
        return generator.generate();
    }
}
