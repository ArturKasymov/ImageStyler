package app;

import Views.core.BaseView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static Utils.Constants.*;


public class ViewManager extends Application {
    private Stage stage;

    private Parent LoginView, RegisterView, MainView;

    @Override
    public void start(Stage primaryStage){
        this.stage = primaryStage;
        initViews();
        initStage();

    }

    private void initStage(){
        stage.setTitle(TITLE);

        stage.setScene(new Scene(LoginView, WIDTH, HEIGHT));
        stage.show();
    }

    private void setView(Parent view) {
        stage.getScene().setRoot(view);
    }

    public void changeViewTo(BaseView targetView){

        switch (targetView.getViewID())
        {
            case LOGIN_VIEW:
                setView(LoginView);
                break;
            case REGISTER_VIEW:
                setView(RegisterView);
                break;
            case MAIN_VIEW:
                setView(MainView);
                break;
        }

    }

    private Parent loadView(String path) throws IOException {
        String layouts = "/Layouts";
        URL url = ViewManager.class.getResource(layouts+path);
        FXMLLoader loader = new FXMLLoader(url);
        Parent view = loader.load();
        BaseView controller = loader.getController();
        controller.setViewManager(this);
        return view;
    }

    private void initViews() {
        try {
            LoginView = loadView("/LoginView.fxml");
            RegisterView = loadView("/RegisterView.fxml");
            MainView = loadView("/MainView.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
