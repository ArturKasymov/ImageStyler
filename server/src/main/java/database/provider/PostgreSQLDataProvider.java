package database.provider;

import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static util.SQLiteQueries.*;

public class PostgreSQLDataProvider {

    private Connection connection;

    public PostgreSQLDataProvider( String dbname, @NotNull String username, @NotNull String password, String IP ,int port) {
        try {
            connection = DriverManager.getConnection(String.format("jdbc:postgresql:%s:%d/%s",IP,port,dbname),username,password);
        } catch (SQLException e) {
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
}
