package Utils.controls;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import Utils.Constants;
import Views.Interfaces.MainView;
import app.AppManager;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;

import static Utils.Constants.SORT_BY.*;

public class ImagesListView extends VBox {
    private MainView view;
    @FXML
    private TextField imagesSearch;

    @FXML
    private ListView<UserImage> imagesListView;

    @FXML
    private ComboBox<String> sortBy;

    private Animation hidePane;
    private Animation showPane;

    private ObservableList<UserImage> userImages;

    private Constants.SORT_BY currentSort = NAME_ASC;

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

    public boolean notifyList(UserImage userImage, boolean insert) {
        if (insert) {
            userImages.add(userImage);
            userImages.sort(getComparator(currentSort));
            updateImagesList(userImages);
            return false;
        } else {
            userImages.remove(userImage);
            updateImagesList(userImages);
            if (userImages.size()>0) {
                view.setResultImage(userImages.get(0));
                return false;
            } else {
                view.setResultImage(null);
                return true;
            }
        }
    }

    private void updateImagesList(ObservableList<UserImage> userImages) {
        imagesListView.setCellFactory(x->null);
        imagesListView.setCellFactory(x->new ImageListCell());
        imagesListView.setItems(userImages);
        checkForVerticalBar();
    }

    private Comparator<UserImage> getComparator(Constants.SORT_BY sortMode) {
        switch (sortMode) {
            case NAME_ASC:
                return Comparator.comparing(UserImage::getImageName);
            case NAME_DESC:
                return (o1, o2) -> o2.getImageName().compareTo(o1.getImageName());
            case DATE_ASC:
                return Comparator.comparing(UserImage::getImageDate);
            case DATE_DESC:
                return (o1, o2) -> o2.getImageDate().compareTo(o1.getImageDate());
        }
        return null;
    }

    private void checkForVerticalBar() {
        // TODO: listen for visibility of bar, not for elements number
        if (userImages.size()>=10) {
            imagesListView.setCellFactory(x->new ImageListCell() {
                {
                    changeDateLabelPosition(105.0);
                }
            });
        } else {
            imagesListView.setCellFactory(x->new ImageListCell() {
                {
                    changeDateLabelPosition(115.0);
                }
            });
        }
    }

    public void initList(){
        try {
            userImages.setAll(view.getUserImagesList());
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        userImages.sort(getComparator(currentSort));
        updateImagesList(userImages);
        if (userImages.size()>0) {
            imagesListView.getSelectionModel().selectFirst();
            view.setResultImage(userImages.get(0));
        }
    }

    @FXML
    public void initialize() {
        userImages = FXCollections.observableArrayList();
        updateImagesList(userImages);

        imagesListView.setSkin(new ListViewSkin(imagesListView) {
            @Override
            public int getItemCount() {
                int r = super.getItemCount();
                return r==0 ? 1 : r;
            }
        });


        imagesSearch.setFocusTraversable(false);
        sortBy.setFocusTraversable(false);
        imagesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) view.setResultImage(newValue);
        });

        imagesSearch.setOnKeyReleased(event -> {
            ObservableList<UserImage> realTimeSearch = userImages.filtered(image -> image.getImageName().indexOf(imagesSearch.getCharacters().toString())==0);
            updateImagesList(realTimeSearch);
            imagesListView.refresh();
        });

        sortBy.valueProperty().addListener((observable, oldValue, newValue) -> {
            userImages.sort(getComparator(getSortMode(newValue)));
            updateImagesList(userImages);
        });
    }



    private Constants.SORT_BY getSortMode(String userChoice) {
        switch (userChoice) {
            case "Name (a-z)":
                return NAME_ASC;
            case "Name (z-a)":
                return NAME_DESC;
            case "Date (0-9)":
                return DATE_ASC;
            case "Date (9-0)":
                return DATE_DESC;
        }
        return null;
    }

    public void cleanList() {
        userImages.clear();
        imagesListView.setCellFactory(x->null);
    }
}
