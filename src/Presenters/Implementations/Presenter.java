package Presenters.Implementations;

public class Presenter {
    private static Presenter instance;

    Presenter getInstance(){
        if(instance==null) instance=new Presenter();
        return instance;
    }
    
}
