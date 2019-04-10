package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager extends Application {
    private Stage stage;
    private static final String pre = "..";
    private static final String TITLE = "Hello UJ";
    private static final int WIDTH = 300;
    private static final int HEIGHT = 275;

    private Parent LoginView, RegisterView, MainView;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage = primaryStage;

        initViews();

        setView(LoginView);
    }

    public void setView(Parent view) {
        stage.setTitle(TITLE);
        stage.setScene(new Scene(view, WIDTH, HEIGHT));
        stage.show();
    }

    private Parent loadView(String path) throws IOException {
        String layouts = "/Layouts";
        return FXMLLoader.load(ViewManager.class.getResource(pre + layouts + path));
    }

    private void initViews() throws IOException {
        LoginView = loadView("/LoginView.fxml");
        RegisterView = loadView("/RegisterView.fxml");
        MainView = loadView("/MainView.fxml");
    }

    public Parent getRegisterView() {
        return RegisterView;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
