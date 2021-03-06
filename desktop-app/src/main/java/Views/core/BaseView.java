package Views.core;

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

    protected boolean checkCurrentView(){
        return appManager.getCurrentView()==getViewID();
    }
}
