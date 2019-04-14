package Utils.controls;

import app.ViewManager;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.net.URL;

public class ImagesListView extends VBox {
    @FXML
    private TextField imagesSearch;

    @FXML
    private ListView<Image> imagesListView;

    private Animation hidePane;
    private Animation showPane;

    public ImagesListView() {
        try {
            attachView("/ImagesListView.fxml", this, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hide() {
        if (hidePane==null) {
            final double startWidth = getWidth();
            hidePane = new Transition() {
                {
                    setCycleDuration(Duration.millis(250));
                }

                @Override
                protected void interpolate(double frac) {
                    final double delta = startWidth * frac;
                    setTranslateX(-delta);
                }
            };
            hidePane.onFinishedProperty().set(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    setVisible(false);
                }
            });
        }
        hidePane.play();
    }

    public void show() {
        if (showPane==null) {
            final double startWidth = getWidth();
            showPane = new Transition() {
                {
                    setCycleDuration(Duration.millis(250));
                }
                @Override
                protected void interpolate(double frac) {
                    final double delta = startWidth * (1.0 - frac);
                    setTranslateX(-delta);
                }
            };
        }
        setVisible(true);
        showPane.play();
    }

    private Parent attachView(String path, Object controller, Object root) throws IOException {
        String layouts = "/Layouts";
        URL url = ViewManager.class.getResource(layouts+path);
        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        loader.setRoot(root);
        return loader.load();
    }

    @FXML
    public void initialize() {

    }
}
