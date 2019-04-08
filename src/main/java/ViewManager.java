import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewManager extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println(ViewManager.class.getResource("").toString());

        Parent root = FXMLLoader.load(ViewManager.class.getResource("Layouts/LoginView.fxml"));
        primaryStage.setTitle("Hello UJ");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
