package Views.Implementations;

import Presenters.GeneratorPresenter;
import Views.Interfaces.GeneratorView;
import Views.core.BaseView;
import Views.core.ViewByID;
import app.AppManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class GeneratorViewImpl extends BaseView implements GeneratorView {

    private GeneratorPresenter presenter;
    private BaseView toggler;
    public GeneratorViewImpl() {
        this.presenter = new GeneratorPresenter(this);
    }

    @Override
    public ViewByID getViewID() { return ViewByID.GENERATOR_VIEW; }

    @FXML
    private ImageView contentImage;

    @FXML
    private ImageView styleImage;

    @FXML
    private ImageView generatedImage;

    @FXML
    private Button generateButton;

    @FXML
    private TextField photoName;

    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        //TODO Rewrite
        URL url = AppManager.class.getResource("/TestImages/img1.png");
        File img = new File(url.getFile());
        Image image = new Image(img.toURI().toString());

        contentImage.setImage(image);
        styleImage.setImage(image);
        generatedImage.setImage(image);
        setOnImageClick(contentImage);
        setOnImageClick(styleImage);
        setOnSaveButtonClick();
    }

    @FXML
    public void onGenerate(ActionEvent e) {
        presenter.generate();
    }

    private void setOnImageClick(final ImageView imgView) {
        imgView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser chooser = new FileChooser();
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                chooser.getExtensionFilters().addAll(extFilterPNG, extFilterJPG);

                File file = chooser.showOpenDialog(null);
                try {
                    BufferedImage buffimage = ImageIO.read(file);
                    Image image = SwingFXUtils.toFXImage(buffimage, null);
                    imgView.setImage(image);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });
    }

    private void setOnSaveButtonClick() {
        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (photoName.getCharacters().length()>0) {
                    presenter.saveGeneratedImage(generatedImage.getImage(), photoName.getCharacters().toString(), new Date());
                } else {
                    showNoPhotoNameAlert();
                }
            }
        });
    }

    private void showNoPhotoNameAlert() {
        throw new RuntimeException();
    }

    public void setViewsToggler(BaseView view) {
        this.toggler = view;
    }

}
