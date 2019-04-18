package Model.Interactors;

import Model.Database.provider.SQLiteLocalDataProvider;


import java.util.Random;

import static Utils.Constants.APP_ROOT_DIRECTORY;
import static Utils.Constants.LOCAL_DATABASE_NAME;

public class Interactor implements GeneratorInteractor, LoginInteractor, MainInteractor, RegisterInteractor {

    private static Interactor instance;

    private SQLiteLocalDataProvider dataProvider;

    private Interactor(){
        this.dataProvider=new SQLiteLocalDataProvider(APP_ROOT_DIRECTORY+"\\"+LOCAL_DATABASE_NAME);
    }

    public static Interactor getInstance(){
        if(instance==null) instance=new Interactor();
        return instance;
    }

    @Override
    public boolean checkUserData(CharSequence login, CharSequence password){
        return new Random().nextBoolean();
    }
}
