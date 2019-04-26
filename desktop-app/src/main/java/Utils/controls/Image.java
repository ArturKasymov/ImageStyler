package Utils.controls;

public class Image {
    private String imageName;
    private String imageUrl;
    private String imageDate;

    public Image() {

    }

    public Image(String name, String url, String date) {
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

    public void setImageDate(String date) { imageDate = date; }

    public String getImageDate() { return imageDate; }


}
