package Utils.controls;

import java.util.Date;

public class Image {
    private String imageName;
    private String imageUrl;
    private Date imageDate;

    public Image() {

    }

    public Image(String name, String url, Date date) {
        setImageName(name);
        setImageUrl(url);
        setImageDate(date);
    }

    public void setImageUrl(String url) {
        imageUrl = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageName(String name) {
        imageName = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageDate(Date date) { imageDate = date; }

    public Date getImageDate() { return imageDate; }

    public void clear() {
        imageName = "";

    }
}
