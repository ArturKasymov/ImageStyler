package Model.Database.provider;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDataProvider {
    private Connection connection;

    public SQLiteDataProvider(@NotNull String dbname) {

        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbname));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
