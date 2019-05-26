package model;

import model.database.entity.User;
import server.ServerManager;
import util.Constants;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.Date;

public interface ClientInteractor {
    int insertUser(String userName, String password) throws SQLException;
    int insertImage(String imageName, int userID, long imageDate);
    User getUser(String username);
    BufferedImage generateImage(BufferedImage contentImage, int styleID, Constants.NEURAL_NET net, double d);
    void checkUserDir(String userPath);
    String getUserImagesListString(int userID);
    void deleteUserImage(int imageID, String userPath);
    boolean changePassword(int userID, String oldPassword, String newPassword);
}
