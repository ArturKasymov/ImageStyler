package Utils.controls;

import Model.Database.Entity.UserImage;
import app.AppManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

public class ImageListCell extends ListCell<UserImage> {

    @FXML
    private Label photoName;

    @FXML
    private Label inProgress;

    @FXML
    private Label date;

    private UserImage userImage;

    private Node view;

    public ImageListCell() {
        try {
            view = attachView("/ImageListCell.fxml", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(UserImage elem, boolean empty) {
        super.updateItem(elem, empty);
        if (elem != null) {
            userImage.setImageName(elem.getImageName());
            photoName.setText(elem.getImageName());
            //System.out.println(elem.getImageName()+" "+elem.getIsDownloaded());
            if (elem.getIsDownloaded()&&inProgress.getText().equals("Wait...")) inProgress.setText("");
            else if (!elem.getIsDownloaded()&&inProgress.getText().equals("")) inProgress.setText("Wait...");
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(elem.getImageDate());
            date.setText(formattedDate);
        } else {
            userImage = new UserImage();
            setGraphic(view);
        }
    }

    private Parent attachView(String path, Object controller) throws IOException {
        String layouts = "/Layouts";
        URL url = AppManager.class.getResource(layouts+path);
        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        return loader.load();
    }

    @FXML
    public void initialize() {
    }

    public void changeDateLabelPosition(double newMargin) {
        AnchorPane.setLeftAnchor(date, newMargin);
    }
}
