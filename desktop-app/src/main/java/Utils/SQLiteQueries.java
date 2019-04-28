package Utils;

public class SQLiteQueries {
    public final static String CHECK_USERS="CREATE TABLE IF NOT EXISTS users (\n" +
            "    id_user INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    user_name VARCHAR(32) NOT NULL UNIQUE,\n" +
            "    password_hash VARCHAR(32) NOT NULL\n" +
            ");\n";

    public final static String CHECK_USER_IMAGES="CREATE TABLE IF NOT EXISTS user_images (\n" +
            "    id_image INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    image_name VARCHAR(32) NOT NULL,\n" +
            "\n" +
            "    id_user INTEGER NOT NULL REFERENCES users(id_user),\n" +
            "    image_date TIMESTAMP NOT NULL DEFAULT current_timestamp,\n" +
            "    is_downloaded INTEGER NOT NULL\n"+
            ");\n";

    public final static String CHECK_STYLES="CREATE TABLE IF NOT EXISTS style_feature_vectors (\n" +
            "\tid_vector INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\tstyle_name VARCHAR(32) NOT NULL,\t\n" +
            "\tvector_value VARCHAR(4294967296)\n" +
            ");";
    public final static String INSERT_USER="INSERT INTO users(user_name,password_hash) VALUES(?,?)";
    public final static String GET_USER_NAMES="SELECT user_name FROM users";
    public final static String GET_USER="SELECT id_user, user_name, password_hash FROM users WHERE user_name= ?";
    public final static String INSERT_IMAGE = "INSERT INTO user_images(image_name,id_user,image_date,is_downloaded) VALUES(?,?,?,?)";
    public final static String GET_USER_IMAGES="SELECT id_image,image_name,id_user,image_date,is_downloaded FROM user_images WHERE id_user= ?";
    public final static String GET_LAST_ROW_ID="SELECT last_insert_rowid();";
}
