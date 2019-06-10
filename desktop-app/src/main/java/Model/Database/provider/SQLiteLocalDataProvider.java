package Model.Database.provider;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Utils.SQLiteQueries.*;

public class SQLiteLocalDataProvider {
    private Connection connection;

    public SQLiteLocalDataProvider(String dbname) {
        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbname));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * CREATE NEEDED TABLES
     */

    public void checkTables(){
        try {
            Statement stmt=connection.createStatement();
            stmt.execute(CHECK_USERS);
            stmt.execute(CHECK_USER_IMAGES);
            stmt.execute(CHECK_STYLES);
        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }

    /**
     * INSERT FRESHLY GENERATED IMAGE IN DB
     * @param imageID id of the image at server db
     * @param imageName entered by user
     * @param userID id of the user
     * @param date generation date
     * @param isDownloaded whether it is downloaded
     */

    public void insertUserImage(int imageID, String imageName, int userID, Date date, boolean isDownloaded) {
        System.out.println(imageID);
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_IMAGE);
            pstmt.setInt(1, imageID);
            pstmt.setString(2,imageName);
            pstmt.setInt(3,userID);
            pstmt.setDate(4,new java.sql.Date(date.getTime()));
            pstmt.setBoolean(5,isDownloaded);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET THE GALLERY OF THE USER currentUser
     * @param currentUserId id of the user
     * @return arrayList of images
     */

    public ArrayList<UserImage> getUserImages(int currentUserId) {
        try {
            System.out.println(currentUserId);

            PreparedStatement pstmt = connection.prepareStatement(GET_USER_IMAGES);
            pstmt.setInt(1,currentUserId);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<UserImage> userImages=new ArrayList<>();
            while(rs.next()) {
                userImages.add(new UserImage(rs.getInt("id_image"),
                        rs.getString("image_name"),
                        rs.getInt("id_user"),
                        rs.getDate("image_date"),
                        rs.getBoolean("is_downloaded")
                        ,false));
            }
            System.out.println(userImages.size());
            return userImages;
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * GET THE IDs of user's images
     * @param currentUserID id of the user
     * @return arrayList of IDs
     */

    public ArrayList<Integer> getUserImagesID(int currentUserID){
        try {
            PreparedStatement pstmt = connection.prepareStatement(GET_CACHED_IMAGES_ID);
            pstmt.setInt(1,currentUserID);
            ResultSet rs = pstmt.executeQuery();

            ArrayList<Integer> userImagesID=new ArrayList<>();
            while(rs.next()) {
                userImagesID.add(rs.getInt(1));
            }
            return userImagesID;
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * GET THE IDs of all the downloaded from server images
     * @param currentUserID id of the user
     * @return IDs of the images
     */

    public ArrayList<Integer> getDownloadedImagesID(int currentUserID){
        try {
            PreparedStatement pstmt = connection.prepareStatement(GET_DOWNLOADED_IMAGES_ID);
            pstmt.setInt(1,currentUserID);
            pstmt.setBoolean(2,true);

            ResultSet rs = pstmt.executeQuery();

            ArrayList<Integer> userImagesID=new ArrayList<>();
            while(rs.next()) {
                userImagesID.add(rs.getInt(1));
            }
            return userImagesID;
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DELETE THE IMAGE FROM DB
     * @param imageID id of the image
     */

    public void deleteUserImage(int imageID) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(DELETE_USER_IMAGE);
            pstmt.setInt(1, imageID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * UPDATE THE STATUS OF THE FRESHLY DOWNLOADED IMAGE
     * @param imageID
     */

    public void updateUserImageIsDownloaded(int imageID) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(UPDATE_USER_IMAGE_IS_DOWNLOADED);
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, imageID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
