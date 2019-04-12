package Views.Implementations;

import Views.core.BaseView;
import Views.core.ViewByID;

public class MainViewImpl extends BaseView {
    @Override
    public ViewByID getViewID() {
        return ViewByID.MAIN_VIEW;
    }
}
