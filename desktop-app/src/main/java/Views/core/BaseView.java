package Views.core;


import Utils.controls.Image;
import javafx.scene.Parent;
import app.AppManager;


public abstract class BaseView extends Parent {

    AppManager appManager;

    public abstract ViewByID getViewID();

    public void setAppManager(AppManager appManager){
        this.appManager = appManager;
    }

    public AppManager getAppManager() {
        return this.appManager;
    }

    protected void changeViewTo(BaseView baseView) {
        appManager.changeViewTo(baseView);
    }

    public void initViewData(){}

}
