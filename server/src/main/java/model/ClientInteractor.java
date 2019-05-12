package model;

import model.database.entity.User;

import java.sql.SQLException;
import java.util.Date;

public interface ClientInteractor {
    int insertUser(String userName, String password) throws SQLException;
    int insertSession(int userID, Date lastUpdate);
    User getUser(String username);
}
