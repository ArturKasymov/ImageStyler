package util;

public class PostgreSQLQueries {
    public static final String CHECK_USERS = "create table IF NOT EXISTS users (\n" +
            "id_user serial primary key,\n" +
            "user_name varchar(32) not null unique,\n" +
            "password_hash varchar(256) not null\n" +
            ");";

    public static final String CHECK_IMAGES=" CREATE TABLE IF NOT EXISTS user_images (\n" +
            "id_image serial PRIMARY KEY,\n" +
            "image_name VARCHAR(32) NOT NULL,\n" +
            "id_user INTEGER REFERENCES users,\n" +
            "image_date TIMESTAMP DEFAULT NOW(),\n"+
            "image_status boolean NOT NULL\n"+
            ");";

    public static final String INSERT_USER="INSERT INTO users(user_name,password_hash) " +
            "VALUES (?,?) RETURNING id_user;";

    public static final String GET_USER_HASH="SELECT * FROM users WHERE user_name=?;";

    public static final String INSERT_IMAGE="INSERT INTO user_images(image_name,id_user,image_date,image_status) " +
            "VALUES(?,?,?,?) RETURNING id_image;";

}
