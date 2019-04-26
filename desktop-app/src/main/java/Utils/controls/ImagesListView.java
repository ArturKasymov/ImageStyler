package Utils.controls;

import Presenters.MainPresenter;
import Views.Implementations.MainViewImpl;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import app.AppManager;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class ImagesListView extends VBox {
    private MainView view;
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

    public void setView(MainView view) {
        this.view = view;
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
        URL url = AppManager.class.getResource(layouts+path);
        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        loader.setRoot(root);
        return loader.load();
    }

    @FXML
    public void initialize() {
        imagesListView.setCellFactory(x->new ImageListCell());

        ObservableList<Image> items = FXCollections.observableArrayList();
        items.add(new Image("abc", "/TestImages/la_muse.jpg", "2014-02-21"));
        items.add(new Image("def", "/TestImages/rain_princess.jpg", "2019-04-26"));
        imagesListView.setItems(items);

        imagesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
                view.setResultImage(newValue);
            }
        });
    }
}
