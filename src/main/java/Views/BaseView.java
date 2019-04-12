package Views;

import Views.core.ViewByID;
import app.ViewManager;
import javafx.scene.Parent;



public abstract class BaseView extends Parent {

    ViewManager viewManager;

    public abstract ViewByID getViewID();

    public void setViewManager(ViewManager viewManager){
        this.viewManager=viewManager;
    }

    protected void changeViewTo(BaseView baseView) {
        viewManager.changeViewTo(baseView);
    }
}
