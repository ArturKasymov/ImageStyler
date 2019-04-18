package Model.Database.provider;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

import static Utils.SQLiteQueries.CHECK_TABLES;

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

}
