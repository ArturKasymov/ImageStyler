package model.database.provider;

import model.database.entity.User;

import java.sql.*;

import static util.PostgreSQLQueries.*;

public class PostgreSQLDataProvider {

    private Connection connection;

    public PostgreSQLDataProvider( String dbname, String username,  String password, String IP ,int port) {
        try {
            connection = DriverManager.getConnection(String.format("jdbc:postgresql:%s:%d/%s",IP,port,dbname),username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkTables()  {
        try {
            Statement stmt=connection.createStatement();
            stmt.execute(CHECK_USERS);
            stmt.execute(CHECK_IMAGES);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertUser(String userName, String passwordHash) throws SQLException {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_USER);

            //TODO logs here
            //System.out.println(userName);
            //System.out.println(passwordHash);

            pstmt.setString(1,userName);
            pstmt.setString(2,passwordHash);
            pstmt.execute();

            ResultSet rs =  pstmt.getResultSet();
            rs.next();
            return rs.getInt(1);
    }

    public User getUser(String username){
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(GET_USER_HASH);

            pstmt.setString(1,username);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if(rs.isClosed()) return null;
            return new User(rs.getInt(1),rs.getString(2),rs.getString(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insertImage(String imageName, int userID, Date imageDate, boolean imageStatus){
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_IMAGE);
            pstmt.setString(1,imageName);
            pstmt.setInt(2,userID);
            pstmt.setDate(3,imageDate);
            pstmt.setBoolean(4,imageStatus);

            pstmt.execute();

            ResultSet rs = pstmt.getResultSet();
            rs.next();
            return rs.getInt(1);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getUserImagesListString(int userID) {

        try {
            PreparedStatement pstmt = connection.prepareStatement(GET_USER_IMAGES);
            pstmt.setInt(1,userID);
            ResultSet rs = pstmt.executeQuery();
            int counter=0;
            StringBuilder sb=new StringBuilder();
            while(rs.next()) {
                counter++;
                sb.append(" ").append(rs.getInt(1)).append(" ").append(rs.getString(2)).append(" ")
                        .append(rs.getDate(3).getTime());
            }
            sb.insert(0,counter);
            return sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
