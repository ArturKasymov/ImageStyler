package Views.Implementations;

import Model.Database.Entity.UserImage;
import Presenters.MainPresenter;
import Utils.Constants;
import Utils.controls.ImagesListView;
import Views.Interfaces.GeneratorView;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import Views.core.ViewByID;
import app.AppManager;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Utils.Constants.IN_PROGRESS_IMAGE;

public class MainViewImpl extends BaseView implements MainView {
    private MainPresenter presenter;
    public MainViewImpl() { this.presenter = new MainPresenter(this); }

    @Override
    public ViewByID getViewID() {
        return ViewByID.MAIN_VIEW;
    }

    @FXML
    private GridPane base;

    private Parent generatorView = null;
    private GeneratorView generatorCtrl = null;

    @FXML
    private ToolBar toolbar;

    @FXML
    private Button goToGenerateButton;

    @FXML
    private Button fullButton;

    @FXML
    private ImagesListView imagesListView;

    @FXML
    private ImageView resultImage;

    @FXML
    private VBox rightPane;

    @FXML
    private HBox contentBox;

    @FXML
    private DialogPane settingsDialog;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField onceMoreNewPasswordField;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Label username;

    @FXML
    private Label photoName;

    @FXML
    private Button deleteImageButton;

    @FXML
    private ComboBox<String> neuralNetChoice;

    private Constants.NEURAL_NET defaultNeuralNet = Constants.NEURAL_NET.TRANSFORMER;

    private UserImage currentImage;

    private Transition rightPaneTransitionFullMode;
    private Transition rightPaneTransitionShowMode;
    private double width;

    @FXML
    public void initialize() {
        presenter.initCallback();

        imagesListView.setView(this);
        oldPasswordField.setOnKeyPressed(event -> maybeChange(event));

        newPasswordField.setOnKeyPressed(event -> maybeChange(event));

        onceMoreNewPasswordField.setOnKeyPressed(event -> maybeChange(event));

        oldPasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        onceMoreNewPasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        changePasswordButton.setDisable(true);

        neuralNetChoice.valueProperty().addListener((observable, oldValue, newValue) -> changeDefaultNeuralNet(newValue));

        deleteImageButton.setDisable(true);
    }

    @Override
    public void initViewData() {
        presenter.initUserData();
        imagesListView.initList();
    }

    @FXML
    protected void onLogOut(ActionEvent e) {
        Platform.runLater(()->{
            setResultImage(null);
            presenter.logout(false);
            imagesListView.cleanList();
            if (goToGenerateButton.getText().equals("-")) onGoToGenerate();
            if (fullButton.getText().equalsIgnoreCase("show")) onFull();

        });
    }

    @FXML
    protected void onCleanCache(ActionEvent e) {
        getAppManager().asyncTask(()->presenter.cleanCache());
    }

    @FXML
    protected void onProfSettings(ActionEvent e) {
        showSettingsScreen(true);
    }

    @FXML
    protected void onFull() {
        if (fullButton.getText().equals("HIDE")) {
            imagesListView.hide();
            if (rightPaneTransitionFullMode == null) {
                final double startWidth = rightPane.getWidth();
                final double endWidth = (base.getWidth()+startWidth)/2;
                width = endWidth;
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
                final double d = startWidth - endWidth;
                rightPaneTransitionShowMode = new Transition() {
                    {
                        setCycleDuration(Duration.millis(250));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double delta = (endWidth - startWidth) * (frac);
                        rightPane.setTranslateX(d+delta);
                    }
                };
            }
            rightPaneTransitionShowMode.play();
            goToFullMode(false);
            fullButton.setText("HIDE");
        }
    }

    @FXML
    protected void onCloseSettingsDialog() {
        showSettingsScreen(false);
        clearPasswordFields();
    }

    @FXML
    protected void onChangePassword() {
        showSettingsScreen(false);
        clearPasswordFields();
    }

    @FXML
    protected void onGoToGenerate() {
        if (goToGenerateButton.getText().equals("+")) {
            try {
                if (generatorView==null) {
                    FXMLLoader loader = new FXMLLoader(getAppManager().getClass()
                            .getResource("/Layouts/GeneratorView.fxml"));
                    generatorView = loader.load();
                    generatorCtrl = loader.getController();
                    generatorCtrl.setViewsToggler(this);
                    BaseView genCtrl = loader.getController();
                    genCtrl.setAppManager(getAppManager());
                }
                HBox.setHgrow(generatorView, Priority.SOMETIMES);
                contentBox.getChildren().setAll(generatorView);
                goToGenerateButton.textProperty().setValue("-");
                fullButton.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            contentBox.getChildren().setAll(imagesListView, rightPane);
            goToGenerateButton.textProperty().setValue("+");
            fullButton.setDisable(false);
            imagesListView.requestFocus();
        }
    }

    @Override
    public void rollBackToMain() {
        onGoToGenerate();
        imagesListView.requestFocus();
    }

    @Override
    public void goToLogin() {
        changeViewTo(new LoginViewImpl());
    }

    @Override
    public void setResultImage(UserImage newUserImage) {
        if (newUserImage==null) {
            resultImage.setImage(null);
            photoName.setText("Hello UJ");
            deleteImageButton.setDisable(true);
        } else {
            String path = newUserImage.getImageUrl();
            this.currentImage = newUserImage;
            photoName.setText(newUserImage.getImageName());
            deleteImageButton.setDisable(false);

            if(!changeImage(resultImage, path)) {
                setInProgress(newUserImage);
                getImageFromServer(newUserImage.getImageID());
            }
        }
    }

    @Override
    public void setInProgress(UserImage usrImg) {
        try {
            resultImage.setImage(SwingFXUtils.toFXImage(ImageIO.read(AppManager.class.getResource(IN_PROGRESS_IMAGE)), null));
            photoName.setText(usrImg.getImageName());
            deleteImageButton.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyDownload(int imageID) {
        if(checkCurrentView())
            imagesListView.notifyDownload(imageID, (currentImage==null)?-1:currentImage.getImageID());
    }

    private boolean changeImage(ImageView imgView, String path) {
        File img = new File(path);
        if (!img.exists()) return false;
        Image image = new Image(img.toURI().toString());
        imgView.setImage(image);
        return true;
    }

    private void goToFullMode(boolean really) {
        if (really) {
            HBox.setMargin(resultImage, new Insets(20, 0, 0, 0));
        } else {
            HBox.setMargin(resultImage, new Insets(20, 0, 0, 0));
        }
    }

    @FXML
    public void onDeleteImage() {
        presenter.deleteUserImage(this.currentImage.getImageID());
        //if(checkCurrentView())notifyDelete();
    }

    public void notifyDelete(){
        if(checkCurrentView()){
            boolean empty = imagesListView.notifyList(this.currentImage, false);
            if (empty) deleteImageButton.setDisable(true);
        }
    }

    @Override
    public void setUsernameLabel(String s) {
        username.setText(s);
    }

    @Override
    public void notifyList(UserImage savedUserImage) {
        if(checkCurrentView())imagesListView.notifyList(savedUserImage, true);
    }

    @Override
    protected boolean checkCurrentView() {
        return getAppManager().getCurrentView()==getViewID();
    }

    @Override
    public ArrayList<UserImage> getUserImagesList() {
        return presenter.getUserImagesList();
    }

    @Override
    public void getImageFromServer(int imageID) {
        presenter.getImageFromServer(imageID);
    }

    private void buttonToggle() {
        if (filledIn() && passwordsMatch()) changePasswordButton.setDisable(false);
        else changePasswordButton.setDisable(true);
    }

    public void closeSettingsWindow() {
        clearPasswordFields();
        showSettingsScreen(false);
    }

    private void showSettingsScreen(boolean show) {
        base.getChildren().forEach(x->x.setDisable(show));
        settingsDialog.setVisible(show);
        settingsDialog.setDisable(!show);
        resultImage.setOpacity(show ? 0.5 : 1);
        ((GridPane)contentBox.getChildren().filtered(x->x instanceof GridPane).get(0))
                .getChildren().filtered(x->x instanceof GridPane).forEach(x-> {
            ((GridPane)x).getChildren().filtered(y -> y instanceof ImageView).forEach(y->y.setOpacity(show ? 0.5 : 1));
        });
        oldPasswordField.requestFocus();
    }

    private void clearPasswordFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        onceMoreNewPasswordField.clear();
    }

    private void maybeChange(KeyEvent event) {
        if (event==null || (event.getCode().getName().equals("Enter") && filledIn() && passwordsMatch())) {
            changePasswordButton.setDisable(true);
            presenter.changePassword(oldPasswordField.getCharacters(), newPasswordField.getCharacters());
        }
    }

    private boolean filledIn() {
        return oldPasswordField.getCharacters().length()>0 && newPasswordField.getCharacters().length()>0 && onceMoreNewPasswordField.getCharacters().length()>0;
    }

    private boolean passwordsMatch() {
        return newPasswordField.getCharacters().toString().equals(onceMoreNewPasswordField.getCharacters().toString());
    }

    private void changeDefaultNeuralNet(String newValue) {
        switch (newValue.charAt(0)) {
            case 'V':
                defaultNeuralNet = Constants.NEURAL_NET.VGG16;
                break;
            case 'S':
                defaultNeuralNet = Constants.NEURAL_NET.SQUEEZENET;
                break;
            case 'T':
                defaultNeuralNet = Constants.NEURAL_NET.TRANSFORMER;
                break;
            default:
                break;
        }
        generatorCtrl.handleNNChange(defaultNeuralNet);
    }

    public Constants.NEURAL_NET getDefaultNeuralNet() {
        return defaultNeuralNet;
    }

    public void showChangeAlert() {
        changePasswordButton.setDisable(false);
        oldPasswordField.styleProperty().setValue("-fx-border-color: red;");
        newPasswordField.styleProperty().setValue("-fx-border-color: red;");
        onceMoreNewPasswordField.styleProperty().setValue("-fx-border-color: red;");
        oldPasswordField.setOnMouseClicked(event -> {
            oldPasswordField.styleProperty().setValue("");
            newPasswordField.styleProperty().setValue("");
            onceMoreNewPasswordField.styleProperty().setValue("");
        });
        newPasswordField.setOnMouseClicked(event -> {
            oldPasswordField.styleProperty().setValue("");
            newPasswordField.styleProperty().setValue("");
            onceMoreNewPasswordField.styleProperty().setValue("");
        });
        onceMoreNewPasswordField.setOnMouseClicked(event -> {
            oldPasswordField.styleProperty().setValue("");
            newPasswordField.styleProperty().setValue("");
            onceMoreNewPasswordField.styleProperty().setValue("");
        });
        oldPasswordField.setOnKeyPressed(event -> {
            oldPasswordField.styleProperty().setValue("");
            newPasswordField.styleProperty().setValue("");
            onceMoreNewPasswordField.styleProperty().setValue("");
        });
        newPasswordField.setOnKeyPressed(event -> {
            oldPasswordField.styleProperty().setValue("");
            newPasswordField.styleProperty().setValue("");
            onceMoreNewPasswordField.styleProperty().setValue("");
        });
        onceMoreNewPasswordField.setOnKeyPressed(event -> {
            oldPasswordField.styleProperty().setValue("");
            newPasswordField.styleProperty().setValue("");
            onceMoreNewPasswordField.styleProperty().setValue("");
        });
        System.out.println("Alert show");
        oldPasswordField.requestFocus();
    }

}
