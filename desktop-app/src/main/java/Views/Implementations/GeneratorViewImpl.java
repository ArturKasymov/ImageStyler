package Views.Implementations;

import Model.Database.Entity.UserImage;
import Presenters.GeneratorPresenter;
import Views.Interfaces.GeneratorView;
import Views.Interfaces.MainView;
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

import static Utils.Constants.NUM_STYLE_IMAGES;

public class GeneratorViewImpl extends BaseView implements GeneratorView {
    private Image styleImages[] = new Image[NUM_STYLE_IMAGES];
    private int styleImageIndex = 0;

    private GeneratorPresenter presenter;
    private MainView toggler;
    public GeneratorViewImpl() {
        this.presenter = new GeneratorPresenter(this);
        loadStyleImages();
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
        presenter.initCallback();
        Image image = styleImages[5];

        contentImage.setImage(image);
        styleImage.setImage(image);
        generatedImage.setImage(image);
        setOnImageClick(contentImage);
        //setOnImageClick(styleImage);
        setOnSaveButtonClick();
    }

    @FXML
    public void onGenerate(ActionEvent e) {
        getAppManager().asyncTask(()->presenter.generate(contentImage.getImage(), styleImageIndex,
                photoName.getCharacters().toString(),toggler.getDefaultNeuralNet()));
    }

    @FXML
    public void onShiftLeftStyleImage() {
        styleImageIndex = (styleImageIndex-1) % NUM_STYLE_IMAGES;
        if (styleImageIndex<0) styleImageIndex += NUM_STYLE_IMAGES;
        styleImage.setImage(styleImages[styleImageIndex]);
    }

    @FXML
    public void onShiftRightStyleImage(){
        styleImageIndex = (styleImageIndex+1) % NUM_STYLE_IMAGES;
        styleImage.setImage(styleImages[styleImageIndex]);
    }

    public void setGeneratedImageView(Image img) {
        generatedImage.setImage(img);
    }

    private Image getImage(String path) {
        URL url = AppManager.class.getResource(path);
        File file = new File(url.getFile());
        Image image = new Image(file.toURI().toString());
        return image;
    }

    private void loadStyleImages() {
        styleImages[0] = getImage("/TestImages/img1.png");
        styleImages[1] = getImage("/TestImages/la_muse.jpg");
        styleImages[2] = getImage("/TestImages/rain_princess.jpg");
        styleImages[3] = getImage("/TestImages/udnie.jpg");
        styleImages[4] = getImage("/TestImages/starry_night_sq.jpg");
        styleImages[5] = getImage("/TestImages/tubingen_sq.jpg");
    }

    private void setOnImageClick(final ImageView imgView) {
        imgView.setOnMouseClicked(event -> {
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
        });
    }

    private void setOnSaveButtonClick() {
        saveButton.setOnMouseClicked(event -> {
            /*if (photoName.getCharacters().length()>0) {
                //presenter.insertGeneratedImage(generatedImage.getImage(), photoName.getCharacters().toString(), new Date());
                getAppManager().asyncTask(()->{
                    presenter.insertGeneratedImage(generatedImage.getImage(), photoName.getCharacters().toString(), new Date());
                });
            } else {
                showNoPhotoNameAlert();
            }*/
        });
    }

    private void showNoPhotoNameAlert() {
        photoName.styleProperty().setValue("-fx-border-color: red;");
        photoName.setOnMouseClicked(event -> photoName.styleProperty().setValue(""));
    }

    public void setViewsToggler(MainView view) {
        this.toggler = view;
    }

    public void notifyList(UserImage savedUserImage) {
        toggler.notifyList(savedUserImage);
    }

}
