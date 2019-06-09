package app;

import Model.Interactors.Interactor;
import Views.core.BaseView;
import Views.core.ViewByID;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.nd4j.linalg.api.ops.executioner.DefaultOpExecutioner;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.nativeblas.NativeOpsHolder;
import sun.util.logging.PlatformLogger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Utils.Constants.*;
import static Views.core.ViewByID.*;


public class AppManager extends Application {
    private Stage stage;

    private ViewByID currentView;

    private Parent LoginView, RegisterView, MainView;
    private BaseView[] controllers = new BaseView[3];
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
        System.out.println(Thread.currentThread().getName());
        interactor=Interactor.getInstance();
        interactor.startSessionManager(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
        com.sun.javafx.util.Logging.getCSSLogger().setLevel(PlatformLogger.Level.OFF);
    }

    private void initStage(){
        stage.setTitle(TITLE);
        stage.setScene(new Scene(LoginView, WIDTH, HEIGHT, Color.TRANSPARENT));
        stage.getIcons().add(new javafx.scene.image.Image(this.getClass().getResource(separator+"TestImages"+separator+"logo_blue.png").toExternalForm()));
        currentView = LOGIN_VIEW;
        stage.show();
    }

    private void initViews() {
        try {
            LoginView = loadView(separator+"LoginView.fxml");
            RegisterView = loadView(separator+"RegisterView.fxml");
            MainView = loadView(separator+"MainView.fxml");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void checkAppRootDir(){
        File dir = new File(APP_ROOT_DIRECTORY);
        if (!dir.exists())dir.mkdirs();
    }

    private void setView(Parent view) {
        Platform.runLater(() -> stage.getScene().setRoot(view));
    }

    public void changeViewTo(BaseView targetView){
        switch (targetView.getViewID())
        {
            case LOGIN_VIEW:
                setView(LoginView);
                currentView=LOGIN_VIEW;
                controllers[0].initViewData();
                break;
            case REGISTER_VIEW:
                setView(RegisterView);
                currentView=REGISTER_VIEW;
                controllers[1].initViewData();
                break;
            case MAIN_VIEW:
                setView(MainView);
                currentView=MAIN_VIEW;
                controllers[2].initViewData();
                break;
        }
    }

    public ViewByID getCurrentView(){
        return currentView;
    }

    private Parent loadView(String path) throws IOException {
        String layouts = separator+"Layouts";
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

    @Override
    public void stop() throws Exception {
        interactor.stopSessionManager();
        executor.shutdown();
        super.stop();
    }
}
