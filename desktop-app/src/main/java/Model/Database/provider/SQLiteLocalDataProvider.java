package Model.Database.provider;

import Model.Database.Entity.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
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

            DatabaseMetaData meta = connection.getMetaData();
            System.out.println("A new database has been created.\nThe driver name is " + meta.getDriverName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkTables(){
        try {
            Statement stmt=connection.createStatement();
            stmt.execute(CHECK_TABLES);
        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String userName, String passwordHash){
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_USER);
            pstmt.setString(1, userName);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }

    public void insertGeneratedImage(Image image, String name, Date date) {
        try {
            //TODO Save image and insert into db
            PreparedStatement NOTCOMPILINGWITHOUTIT = connection.prepareStatement(INSERT_USER);

            /*
            BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
            File file = new File(".")
            ImageIO.write(buffImage, ".png", file);
            PreparedStatement pstmt = connection.prepareStatement(INSERT_GENERATED_IMAGE);
            pstmt.executeUpdate();
            */
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserID(String userName, String passwordHash){
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery(GET_USER_ID);

        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return -1;

    }

    public User getUser(String userName){
        try {
            PreparedStatement pstmt = connection.prepareStatement(GET_USER);
            pstmt.setString(1,userName);
            ResultSet rs = pstmt.executeQuery();
            return new User(rs.getInt("id_user"),
                    rs.getString("user_name"),
                    rs.getString("password_hash")
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

}
