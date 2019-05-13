package Views.Implementations;

import Model.Database.Entity.UserImage;
import Presenters.MainPresenter;
import Utils.Constants;
import Utils.controls.ImagesListView;
import Views.Interfaces.GeneratorView;
import Views.Interfaces.MainView;
import Views.core.BaseView;
import Views.core.ViewByID;
import javafx.animation.Transition;
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
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    @FXML
    private ToolBar toolbar;

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
    private VBox rightPane;

    @FXML
    private HBox contentBox;

    @FXML
    private DialogPane settingsDialog;

    @FXML
    private Button exitSettingsButton;

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

    private Constants.NEURAL_NET defaultNeuralNet = Constants.NEURAL_NET.SQUEEZENET;

    private UserImage currentImage;

    private Transition rightPaneTransitionFullMode;
    private Transition rightPaneTransitionShowMode;
    private double width;

    @FXML
    public void initialize() {
        presenter.initCallback();
        deleteImageButton.setDisable(true);

        changeImage(resultImage, "/TestImages/.10.png");
        imagesListView.setView(this);
        oldPasswordField.setOnKeyPressed(event -> maybeChange(event));

        newPasswordField.setOnKeyPressed(event -> maybeChange(event));

        onceMoreNewPasswordField.setOnKeyPressed(event -> maybeChange(event));

        oldPasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        onceMoreNewPasswordField.textProperty().addListener((observable, oldValue, newValue) -> buttonToggle());

        changePasswordButton.setDisable(true);

        neuralNetChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeDefaultNeuralNet(newValue);
        });
    }

    @Override
    public void initViewData() {
        presenter.initUserData();
        // TODO: TEMP
        //imagesListView.initList();
    }

    @FXML
    protected void onLogOut(ActionEvent e) {
        presenter.logout();
        imagesListView.cleanList();
    }

    @FXML
    protected void onCleanCache(ActionEvent e) {
        presenter.cleanCache();
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
    protected void onGoToGenerate() throws IOException{
        if (goToGenerateButton.getText().equals("+")) {
            try {
                if (generatorView==null) {
                    FXMLLoader loader = new FXMLLoader(getAppManager().getClass()
                            .getResource("/Layouts/GeneratorView.fxml"));
                    generatorView = loader.load();
                    GeneratorView ctrl = loader.getController();
                    ctrl.setViewsToggler(this);
                    BaseView genCtrl = loader.getController();
                    genCtrl.setAppManager(getAppManager());
                }
                contentBox.getChildren().setAll(generatorView);
                goToGenerateButton.textProperty().setValue("-");
                fullButton.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
                throw new IOException("Failed to load GeneratorView.fxml");
            }
        } else {
            contentBox.getChildren().setAll(imagesListView, rightPane);
            goToGenerateButton.textProperty().setValue("+");
            fullButton.setDisable(false);
        }
    }

    @Override
    public void goToLogin() {
        //presenter.unsubscribe();
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
            changeImage(resultImage, path);
            photoName.setText(newUserImage.getImageName());
        }
    }

    private void changeImage(ImageView imgView, String path) {
        File img= new File(path);
        if (img==null) return;
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

    @FXML
    public void onDeleteImage() {
        presenter.deleteImage(this.currentImage);
        boolean empty = imagesListView.notifyList(this.currentImage, false);
        //if (empty) deleteImageButton.setDisable(true);
    }

    @Override
    public void setUsernameLabel(String s) {
        username.setText(s);
    }

    @Override
    public void notifyList(UserImage savedUserImage) {
        imagesListView.notifyList(savedUserImage, true);
    }

    @Override
    public ArrayList<UserImage> getUserImagesList() {
        return presenter.getUserImagesList();
    }

    private void buttonToggle() {
        if (filledIn() && passwordsMatch()) changePasswordButton.setDisable(false);
        else changePasswordButton.setDisable(true);
    }

    private void showSettingsScreen(boolean show) {
        settingsDialog.setVisible(show);
        toolbar.setDisable(show);
        contentBox.setDisable(show);
        resultImage.setOpacity(show ? 0.5 : 1);
        oldPasswordField.requestFocus();
    }

    private void clearPasswordFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        onceMoreNewPasswordField.clear();
    }

    public void showChangeAlert() {
        throw new RuntimeException();
    }

    private void maybeChange(KeyEvent event) {
        if (event==null || (event.getCode().getName().equals("Enter") && filledIn() && passwordsMatch())) {
            changePasswordButton.setDisable(false);
            presenter.changePassword(oldPasswordField.getCharacters(), newPasswordField.getCharacters());
            clearPasswordFields();
            showSettingsScreen(false);
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
            default:
                break;
        }
    }

    public Constants.NEURAL_NET getDefaultNeuralNet() {
        return defaultNeuralNet;
    }

}
