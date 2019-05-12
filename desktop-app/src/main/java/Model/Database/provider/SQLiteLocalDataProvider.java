package Model.Database.provider;

import Model.Database.Entity.User;
import Model.Database.Entity.UserImage;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Utils.SQLiteQueries.*;

public class SQLiteLocalDataProvider {
    private Connection connection;

    public SQLiteLocalDataProvider(@NotNull String dbname) {
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

    public int insertUserImage(String imageName, int userID ,Date date, boolean isDownloaded) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_IMAGE);
            pstmt.setString(1,imageName);
            pstmt.setInt(2,userID);
            pstmt.setDate(3,new java.sql.Date(date.getTime()));
            pstmt.setBoolean(4,isDownloaded);
            pstmt.executeUpdate();
            return connection.createStatement().executeQuery(GET_LAST_ROW_ID).getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertImages(ArrayList<UserImage> images){

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
                        ));
            }
            return userImages;
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUserImage(UserImage deletedImage) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(DELETE_USER_IMAGE);
            pstmt.setInt(1, deletedImage.getImageID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
