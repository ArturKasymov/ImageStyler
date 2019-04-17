package Views.Implementations;

import Presenters.MainPresenter;
import Utils.controls.ImagesListView;
import Views.Interfaces.GeneratorView;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import Views.core.ViewByID;
import javafx.animation.Transition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
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
    private Button goToGenerateButton;

    @FXML
    private Button fullButton;

    @FXML
    private ImagesListView imagesListView;

    @FXML
    private ImageView resultImage;

    @FXML
    private HBox rightPane;

    @FXML
    private HBox contentBox;

    private Transition rightPaneTransitionFullMode;
    private Transition rightPaneTransitionShowMode;
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
    protected void onFull() {
        /*
        if (fullButton.getText().equals("FULL")) {
            imagesListView.hide();
            if (rightPaneTransitionFullMode == null) {
                final double startWidth = rightPane.getWidth();
                width = startWidth;
                final double endWidth = base.getWidth();
                rightPaneTransitionFullMode = new Transition() {
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
            rightPaneTransitionFullMode.play();
            goToFullMode(true);
            fullButton.setText("SHOW");
        } else {
            imagesListView.show();
            if (rightPaneTransitionShowMode == null) {
                final double startWidth = rightPane.getWidth();
                final double endWidth = width;
                rightPaneTransitionShowMode = new Transition() {
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
            rightPaneTransitionShowMode.play();
            goToFullMode(false);
            fullButton.setText("FULL");
        }
        */
    }

    @FXML
    protected void onGoToGenerate() throws IOException{
        if (goToGenerateButton.getText().equals("+")) {
            try {
                FXMLLoader loader = new FXMLLoader(getViewManager().getClass()
                        .getResource("/Layouts/GeneratorView.fxml"));
                Parent generatorView = loader.load();
                GeneratorView ctrl = loader.getController();
                ctrl.setViewsToggler(this);
                contentBox.getChildren().setAll(generatorView);
                goToGenerateButton.textProperty().setValue("-");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
                throw new IOException("Failed to load GeneratroView.fxml");
            }
        } else {
            contentBox.getChildren().setAll(imagesListView, rightPane);
            goToGenerateButton.textProperty().setValue("+");
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

    private void goToFullMode(boolean really) {
        if (really) {
            HBox.setMargin(resultImage, new Insets(20, 0, 0, 0));
        } else {
            HBox.setMargin(resultImage, new Insets(20, 0, 0, 0));
        }
    }

}
