package Model.Database.provider;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
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
