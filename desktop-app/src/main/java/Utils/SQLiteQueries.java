package Utils;

public class SQLiteQueries {
    public final static String CHECK_USERS="CREATE TABLE IF NOT EXISTS users (\n" +
            "    id_user INTEGER PRIMARY KEY,\n" +
            "    user_name VARCHAR(32) NOT NULL UNIQUE\n" +
            ");\n";

    public final static String CHECK_USER_IMAGES="CREATE TABLE IF NOT EXISTS user_images (\n" +
            "    id_image INTEGER PRIMARY KEY,\n" +
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
    public final static String INSERT_USER="INSERT INTO users(id_user,user_name) VALUES(?,?)";
    public final static String GET_USER_NAMES="SELECT user_name FROM users";
    public final static String GET_USER = "SELECT id_user, user_name, password_hash FROM users WHERE user_name= ?";
    public final static String INSERT_IMAGE = "INSERT INTO user_images(id_image,image_name,id_user,image_date,is_downloaded) VALUES(?,?,?,?,?)";
    public final static String GET_USER_IMAGES = "SELECT id_image, image_name, id_user, image_date, is_downloaded FROM user_images WHERE id_user=?";
    public final static String CHANGE_PASSWORD = "UPDATE users SET password_hash=? WHERE user_name=?";

    public final static String DELETE_USER_IMAGE = "DELETE FROM user_images WHERE id_image=?";

    public final static String GET_CACHED_IMAGES_ID="SELECT id_image FROM user_images WHERE id_user=? ORDER BY id_image";

    public final static String UPDATE_USER_IMAGE_IS_DOWNLOADED = "UPDATE user_images SET is_downloaded=? WHERE id_image=?;";
}
