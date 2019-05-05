package app;

import Model.Interactors.Interactor;
import Views.core.BaseView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.nd4j.linalg.api.ops.executioner.DefaultOpExecutioner;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.nativeblas.NativeOpsHolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Utils.Constants.*;


public class AppManager extends Application {
    private Stage stage;

    private Parent LoginView, RegisterView, MainView;
    private BaseView[] controllers=new BaseView[3];
    private Interactor interactor;

    private ScheduledExecutorService executor;

    @Override
    public void start(Stage primaryStage){
        this.stage = primaryStage;
        initModel();
        initViews();
        initStage();
    }

    private void initModel(){
        executor = Executors.newSingleThreadScheduledExecutor();
        checkAppRootDir();
        interactor=Interactor.getInstance();
        interactor.startSessionManager(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
        loadND4Jdata(true);
    }

    private void initStage(){
        stage.setTitle(TITLE);
        stage.setScene(new Scene(LoginView, WIDTH, HEIGHT));
        stage.show();
    }

    private void initViews() {
        try {
            LoginView = loadView("/LoginView.fxml");
            RegisterView = loadView("/RegisterView.fxml");
            MainView = loadView("/MainView.fxml");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void checkAppRootDir(){
        File dir = new File(APP_ROOT_DIRECTORY);
        if (!dir.exists())dir.mkdirs();
    }

    private void setView(Parent view) {
        stage.getScene().setRoot(view);

    }

    public void changeViewTo(BaseView targetView){
        switch (targetView.getViewID())
        {
            case LOGIN_VIEW:
                setView(LoginView);
                controllers[0].initViewData();
                break;
            case REGISTER_VIEW:
                setView(RegisterView);
                controllers[1].initViewData();
                break;
            case MAIN_VIEW:
                setView(MainView);
                controllers[2].initViewData();
                break;
        }
    }

    private Parent loadView(String path) throws IOException {
        String layouts = "/Layouts";
        URL url = AppManager.class.getResource(layouts+path);
        FXMLLoader loader = new FXMLLoader(url);
        Parent view = loader.load();
        BaseView controller = loader.getController();
        saveController(controller);
        controller.setAppManager(this);
        return view;
    }

    private void saveController(BaseView controller){
        switch (controller.getViewID())
        {
            case LOGIN_VIEW:
                controllers[0]=controller;
                break;
            case REGISTER_VIEW:
                controllers[1]=controller;
                break;
            case MAIN_VIEW:
                controllers[2]=controller;
                break;
        }
    }

    public void asyncTask(Runnable task) {
        executor.execute(task);
    }

    public void asyncTaskLater(Runnable task, long delay, TimeUnit unit) {
        executor.schedule(task, delay, unit);
    }

    private void loadND4Jdata(boolean load) {
        if (load) try {
            Nd4jBackend.load();
            NativeOpsHolder.getInstance();
            new DefaultOpExecutioner();

        } catch (Nd4jBackend.NoAvailableBackendException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        interactor.stopSessionManager();
        super.stop();
    }
}
