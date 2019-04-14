package Utils.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Image {
    private final StringProperty imageName= new SimpleStringProperty();
    private final StringProperty imageUrl = new SimpleStringProperty();

    public Image(String name, String url) {
        setImageName(name);
        setImageUrl(url);
    }

    public void setImageUrl(String url) {
        imageUrl.set(url);
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageName(String name) {
        imageUrl.set(name);
    }

    public String getImageName() {
        return imageName.get();
    }
}
