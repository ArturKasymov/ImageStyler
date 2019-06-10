package Model.Database.Entity;

import Utils.annotations.Getter;
import Utils.annotations.Setter;

import java.util.Date;

import static Utils.Constants.APP_ROOT_DIRECTORY;
import static Utils.Constants.separator;


public class UserImage {
    private int imageID;
    private String imageName;
    private int userID;
    private Date imageDate;
    private boolean isDownloaded;
    private boolean isWaiting;

    public UserImage() {}

    public UserImage(int imageID, String name, int userID, Date date, boolean isDownloaded, boolean isWaiting) {
        this.imageID=imageID;
        this.imageName=name;
        this.imageDate=date;
        this.userID=userID;
        this.isDownloaded=isDownloaded;
        this.isWaiting=isWaiting;
    }

    @Getter
    public boolean isWaiting() {
        return isWaiting;
    }

    @Setter
    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    @Getter
    public int getImageID(){
        return imageID;
    }

    @Getter
    public String getImageUrl() {
        return APP_ROOT_DIRECTORY+separator+"."+userID+separator+"."+imageID+".png";
    }

    @Getter
    public String getImageName() {
        return imageName;
    }

    @Setter
    public void setImageName(String name) {
        imageName = name;
    }

    @Getter
    public Date getImageDate() { return imageDate; }

    @Getter
    public boolean getIsDownloaded(){
        return isDownloaded;
    }

    @Setter
    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

}
