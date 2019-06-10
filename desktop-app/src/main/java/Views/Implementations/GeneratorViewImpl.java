package Views.Implementations;

import Model.Database.Entity.UserImage;
import Presenters.GeneratorPresenter;
import Utils.Constants;
import Views.Interfaces.GeneratorView;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import Views.core.ViewByID;
import app.AppManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static Utils.Constants.NUM_STYLE_IMAGES;
import static Utils.Constants.separator;

public class GeneratorViewImpl extends BaseView implements GeneratorView {
    private BufferedImage styleImages[] = new BufferedImage[NUM_STYLE_IMAGES];
    private BufferedImage contentImageDefault;
    private int styleImageIndex = 5;

    private GeneratorPresenter presenter;
    private MainView toggler;
    public GeneratorViewImpl() {
        this.presenter = new GeneratorPresenter(this);
        loadStyleImages();
    }

    @Override
    public ViewByID getViewID() { return ViewByID.GENERATOR_VIEW; }

    @FXML
    private Slider strengthSlider;

    @FXML
    private ImageView contentImage;

    @FXML
    private ImageView styleImage;

    @FXML
    private CheckBox fullSizeCheck;

    @FXML
    private Button generateButton;

    @FXML
    private TextField photoName;

    private boolean preserveSize = false;

    @FXML
    public void initialize() {
        BufferedImage image = styleImages[5];

        setImage(contentImage, contentImageDefault);
        setImage(styleImage, image);
        setOnImageClick(contentImage);

        handleNNChange(Constants.NEURAL_NET.TRANSFORMER);
    }

    @FXML
    public void onGenerate(ActionEvent e) {
        String name = photoName.getCharacters().toString();
        int index = styleImageIndex;
        Image contImg = contentImage.getImage();
        if (photoName.getCharacters().length()>0 && photoName.getCharacters().length()<=Constants.MAX_STRING_LENGTH) {
            getAppManager().asyncTask(()->presenter.generate(contImg, index,
                    name, toggler.getDefaultNeuralNet(), strengthSlider.getValue(), preserveSize));
        } else {
            showNoPhotoNameAlert();
            return;
        }
        photoName.clear();
        setImage(contentImage, contentImageDefault);
        setImage(styleImage, styleImages[5]);
        styleImageIndex = 5;
        strengthSlider.setValue(50);
        toggler.rollBackToMain();
    }

    @FXML
    public void onShiftLeftStyleImage() {
        styleImageIndex = (styleImageIndex-1) % NUM_STYLE_IMAGES;
        if (styleImageIndex<0) styleImageIndex += NUM_STYLE_IMAGES;
        setImage(styleImage, styleImages[styleImageIndex]);
    }

    @FXML
    public void onShiftRightStyleImage(){
        styleImageIndex = (styleImageIndex+1) % NUM_STYLE_IMAGES;
        setImage(styleImage, styleImages[styleImageIndex]);
    }

    private BufferedImage getImage(String path) {
        try {
            URL url = AppManager.class.getResource(path);
            File file = new File(url.getFile());
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadStyleImages() {
        styleImages[0] = getImage(separator+"TestImages"+separator+"img1.png");
        styleImages[1] = getImage(separator+"TestImages"+separator+"la_muse.jpg");
        styleImages[2] = getImage(separator+"TestImages"+separator+"rain_princess.jpg");
        styleImages[3] = getImage(separator+"TestImages"+separator+"udnie.jpg");
        styleImages[4] = getImage(separator+"TestImages"+separator+"starry_night_sq.jpg");
        styleImages[5] = getImage(separator+"TestImages"+separator+"the_scream.png");
        styleImages[6] = getImage(separator+"TestImages"+separator+"composition_vii.jpg");

        contentImageDefault = getImage(separator+"TestImages"+separator+"tubingen_sq.png");
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
                setImage(imgView, buffimage);
            } catch (IOException e) {
            }
        });
    }

    @FXML
    public void onFullSize() {
        preserveSize = !preserveSize;
    }

    private void setImage(ImageView imgView, BufferedImage img) {
        imgView.setImage(SwingFXUtils.toFXImage(img, null));
    }

    private void showNoPhotoNameAlert() {
        photoName.styleProperty().setValue("-fx-border-color: red;");
        photoName.setOnMouseClicked(event -> photoName.styleProperty().setValue(""));
    }

    public void setViewsToggler(MainView view) {
        this.toggler = view;
    }

    @Override
    public void handleNNChange(Constants.NEURAL_NET net) {
        switch (net) {
            case TRANSFORMER:
                strengthSlider.setValue(50);
                strengthSlider.setDisable(true);
                fullSizeCheck.setSelected(true);
                fullSizeCheck.setDisable(true);
                break;
            default:
                strengthSlider.setDisable(false);
                fullSizeCheck.setSelected(false);
                fullSizeCheck.setDisable(false);
                break;
        }
    }

    public void notifyList(UserImage savedUserImage) {
        toggler.notifyList(savedUserImage);
    }

}
