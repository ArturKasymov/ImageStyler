package app;

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
        //System.out.println(LoginView);
        setView(LoginView);
    }

    private void setView(Parent view) {
        stage.setTitle(TITLE);
        stage.setScene(new Scene(view, WIDTH, HEIGHT));
        stage.show();
    }

    public void changeViewTo(Parent targetView){
        setView(targetView);
    }

    private Parent loadView(String path) throws IOException {
        String layouts = "/Layouts";
        URL url = ViewManager.class.getResource(layouts+path);
        FXMLLoader loader = new FXMLLoader(url);

        return loader.load();
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

    public Parent getRegisterView() {
        return RegisterView;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
