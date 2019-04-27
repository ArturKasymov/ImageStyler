package Utils.controls;

import Utils.Constants;
import Views.Interfaces.MainView;
import app.AppManager;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;

public class ImagesListView extends VBox {
    private MainView view;
    @FXML
    private TextField imagesSearch;

    @FXML
    private ListView<Image> imagesListView;

    @FXML
    private ComboBox<String> sortBy;

    private Animation hidePane;
    private Animation showPane;

    private ObservableList<Image> userImages;

    private Constants.SORT_BY currentSort = Constants.SORT_BY.NAME_ASC;

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

    public void notifyList(Image savedImage) {
        userImages.add(savedImage);
        userImages.sort(getComparator(currentSort));
        updateImagesList(userImages);
    }

    private void updateImagesList(ObservableList<Image> images) {
        imagesListView.setCellFactory(x->null);
        imagesListView.setCellFactory(x->new ImageListCell());
        imagesListView.setItems(images);
    }

    private Comparator<Image> getComparator(Constants.SORT_BY sortMode) {
        switch (sortMode) {
            case NAME_ASC:
                return new Comparator<Image>() {
                    @Override
                    public int compare(Image o1, Image o2) {
                        return o1.getImageName().compareTo(o2.getImageName());
                    }
                };
            case NAME_DESC:
                return new Comparator<Image>() {
                    @Override
                    public int compare(Image o1, Image o2) {
                        return o2.getImageName().compareTo(o1.getImageName());
                    }
                };
            case DATE_ASC:
                return new Comparator<Image>() {
                    @Override
                    public int compare(Image o1, Image o2) {

                        return o1.getImageDate().compareTo(o2.getImageDate());
                    }
                };
            case DATE_DESC:
                return new Comparator<Image>() {
                    @Override
                    public int compare(Image o1, Image o2) {
                        return o2.getImageDate().compareTo(o1.getImageDate());
                    }
                };
        }
        return null;
    }

    @FXML
    public void initialize() {

        // TODO: FETCH FROM DATABASE
        userImages = FXCollections.observableArrayList();
        userImages.add(new Image("def", "/TestImages/la_muse.jpg", new Date()));
        Date tmpDate = new Date();
        tmpDate.setYear(114);
        userImages.add(new Image("abc", "/TestImages/rain_princess.jpg", tmpDate));
        userImages.sort(getComparator(currentSort));
        updateImagesList(userImages);

        imagesSearch.setFocusTraversable(false);
        sortBy.setFocusTraversable(false);

        imagesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
                if (newValue!=null) view.setResultImage(newValue);
            }
        });

        imagesSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                ObservableList<Image> realTimeSearch = userImages.filtered(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) {
                        return image.getImageName().indexOf(imagesSearch.getCharacters().toString())==0;
                    }
                });
                updateImagesList(realTimeSearch);
                imagesListView.refresh();
            }
        });

        sortBy.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                userImages.sort(getComparator(getSortMode(newValue)));
                updateImagesList(userImages);
            }
        });
    }

    private Constants.SORT_BY getSortMode(String userChoice) {
        switch (userChoice) {
            case "Name (a-z)":
                return Constants.SORT_BY.NAME_ASC;
            case "Name (z-a)":
                return Constants.SORT_BY.NAME_DESC;
            case "Date (0-9)":
                return Constants.SORT_BY.DATE_ASC;
            case "Date (9-0)":
                return Constants.SORT_BY.DATE_DESC;
        }
        return null;
    }
}
