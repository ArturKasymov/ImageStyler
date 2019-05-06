package model.database.provider;

import java.sql.*;
import java.util.Date;

import static util.PostgreSQLQueries.*;

public class PostgreSQLDataProvider {

    private Connection connection;

    public PostgreSQLDataProvider( String dbname, String username,  String password, String IP ,int port) {
        try {
		//Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(String.format("jdbc:postgresql:%s:%d/%s",IP,port,dbname),username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkTables()  {
        try {
            Statement stmt=connection.createStatement();
            stmt.execute(CHECK_USERS);
            stmt.execute(CHECK_SESSIONS);
            stmt.execute(CHECK_FOTOS);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertUser(String userName, String passwordHash) throws SQLException {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_USER);

            //TODO delete logs
            System.out.println(userName);
            System.out.println(passwordHash);

            pstmt.setString(1,userName);
            pstmt.setString(2,passwordHash);
            pstmt.execute();

            ResultSet rs =  pstmt.getResultSet();
            rs.next();
            return rs.getInt(1);
    }

    public int insertSession(int userID, Date lastUpdate){
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_SESSION);
            pstmt.setInt(1,userID);
            pstmt.setBoolean(2,true);
            pstmt.setDate(3,new java.sql.Date(lastUpdate.getTime()));
            pstmt.execute();

            ResultSet rs= pstmt.getResultSet();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return 0;
    }


}
