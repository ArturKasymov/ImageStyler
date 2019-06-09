package Model.Database.provider;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import javafx.scene.image.Image;

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

    public void insertUser(int userID,String userName){
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_USER);
            pstmt.setInt(1,userID);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changePassword(String userName, String passwordHash) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(CHANGE_PASSWORD);
            pstmt.setString(1, passwordHash);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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


    public User getUser(String userName){
        try {
            PreparedStatement pstmt = connection.prepareStatement(GET_USER);
            pstmt.setString(1,userName);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isClosed()) return null;
            return new User(rs.getInt("id_user"),
                    rs.getString("user_name")
            );
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getLocalUsersNameList(){
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery(GET_USER_NAMES);
            List<String> localUsersNameList= new ArrayList<String>();
            while(rs.next()) {
                localUsersNameList.add(rs.getString("user_name"));
            }
            return localUsersNameList;
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public void deleteUserImage(int imageID) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(DELETE_USER_IMAGE);
            pstmt.setInt(1, imageID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
