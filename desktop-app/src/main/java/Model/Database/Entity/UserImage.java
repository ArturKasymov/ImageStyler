package Model.Database.Entity;

import java.util.Date;

import static Utils.Constants.APP_ROOT_DIRECTORY;


public class UserImage {
    private int imageID;
    private String imageName;
    private int userID;
    private Date imageDate;
    private boolean isDownloaded;

    public UserImage() {

    }

    public UserImage(int imageID, String name, int userID, Date date, boolean isDownloaded) {
        this.imageID=imageID;
        this.imageName=name;
        this.imageDate=date;
        this.userID=userID;
        this.isDownloaded=isDownloaded;
    }

    public int getImageID(){
        return imageID;
    }
    public String getImageUrl() {
        return APP_ROOT_DIRECTORY+"\\."+userID+"\\."+imageID+".jpg";
    }

    public boolean getIsDownloaded(){
        return isDownloaded;
    }

    public void setImageName(String name) {
        imageName = name;
    }

    public String getImageName() {
        return imageName;
    }

    public Date getImageDate() { return imageDate; }

}
