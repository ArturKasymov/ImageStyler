package Views.Implementations;

import Presenters.MainPresenter;
import Utils.controls.ImagesListView;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import Views.core.ViewByID;
import javafx.animation.Transition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainViewImpl extends BaseView implements MainView {
    private MainPresenter presenter;
    public MainViewImpl() { this.presenter = new MainPresenter(this); }

    @Override
    public ViewByID getViewID() {
        return ViewByID.MAIN_VIEW;
    }

    @FXML
    private GridPane base;

    @FXML
    private MenuButton settingsButton;

    @FXML
    private Button generateButton;

    @FXML
    private ImagesListView imagesListView;

    @FXML
    private ImageView contentImage;

    @FXML
    private ImageView styleImage;

    @FXML
    private ImageView resultImage;

    @FXML
    private HBox rightPane;

    private Transition rightPaneTransitionGenMode;
    private Transition rightPaneTransitionGallMode;
    private double width;

    @FXML
    public void initialize() {
        changeImage(resultImage);
    }

    @FXML
    protected void onLogOut(ActionEvent e) {
        presenter.logout();
    }

    @FXML
    protected void onCleanCache(ActionEvent e) {
        presenter.cleanCache();
    }

    @FXML
    protected void onProfSettings(ActionEvent e) {
        presenter.goToSettings();
    }

    @FXML
    protected void onGenerate() {
        if (generateButton.getText().equals("+")) {
            imagesListView.hide();
            if (rightPaneTransitionGenMode == null) {
                final double startWidth = rightPane.getWidth();
                width = startWidth;
                final double endWidth = base.getWidth();
                rightPaneTransitionGenMode = new Transition() {
                    {
                        setCycleDuration(Duration.millis(250));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double delta = (endWidth - startWidth) * frac;
                        rightPane.setTranslateX(-delta);
                    }
                };
            }
            rightPaneTransitionGenMode.play();
            switchToGenerateMode(true);
            generateButton.setText("-");
        } else {
            imagesListView.show();
            if (rightPaneTransitionGallMode == null) {
                final double startWidth = rightPane.getWidth();
                final double endWidth = width;
                rightPaneTransitionGallMode = new Transition() {
                    {
                        setCycleDuration(Duration.millis(250));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double delta = (endWidth - startWidth) * (frac);
                        rightPane.setTranslateX(delta);
                    }
                };
            }
            rightPaneTransitionGallMode.play();
            switchToGenerateMode(false);
            generateButton.setText("+");
        }
    }

    public void goToLogin() {
        //presenter.unsubscribe();
        changeViewTo(new LoginViewImpl());
    }

    private void changeImage(ImageView imgView) {
        File img = new File("/Demko/Projects/ImageStyler/desktop-app/src/main/java/Utils/images/img1.png");
        Image image = new Image(img.toURI().toString());
        imgView.setImage(image);
    }

    private void setImageView(final ImageView img) {
        img.setVisible(true);
        img.setFitHeight(150.0);
        img.setFitWidth(150.0);
        changeImage(img);
        img.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser filechooser = new FileChooser();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                filechooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

                File file = filechooser.showOpenDialog(null);
                try {
                    BufferedImage buffimage = ImageIO.read(file);
                    Image image = SwingFXUtils.toFXImage(buffimage, null);
                    img.setImage(image);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });
        resultImage.setFitWidth(150.0);
        resultImage.setFitHeight(150.0);
        HBox.setMargin(resultImage, new Insets(80.0, 20.0, 80.0, 40.0));
        HBox.setMargin(img, new Insets(80.0, 20.0, 80.0, 20.0));
    }

    private void switchToGenerateMode(boolean to) {
        if (to) {
            setImageView(contentImage);
            setImageView(styleImage);
        } else {
            contentImage.setVisible(false);
            styleImage.setVisible(false);
        }
    }

}
