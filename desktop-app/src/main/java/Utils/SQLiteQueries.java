package Utils;

public class SQLiteQueries {
    public final static String CHECK_TABLES="CREATE TABLE IF NOT EXISTS users (\n" +
            "    id_user INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    user_name VARCHAR(32) NOT NULL UNIQUE,\n" +
            "    password_hash VARCHAR(32) NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS user_fotos (\n" +
            "    id_foto INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    foto_name VARCHAR(32) NOT NULL,\n" +
            "\n" +
            "    id_user INTEGER NOT NULL REFERENCES users(id_user),\n" +
            "    time TIMESTAMP NOT NULL DEFAULT current_timestamp\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS style_feature_vectors (\n" +
            "\tid_vector INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\tstyle_name VARCHAR(32) NOT NULL,\t\n" +
            "\tvector_value VARCHAR(4294967296)\n" +
            ");";
}
