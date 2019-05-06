package util;

public class PostgreSQLQueries {
    public static final String CHECK_USERS = "create table IF NOT EXISTS users (\n" +
            "id_user serial primary key,\n" +
            "user_name varchar(32) not null unique,\n" +
            "password_hash varchar(32) not null\n" +
            ");";
    public static final String CHECK_SESSIONS="create table IF NOT EXISTS sessions (\n" +
            "id_session serial primary key,\n" +
            "id_user integer references users,\n" +
            "isActive boolean,\n" +
            "last_update TIMESTAMP\n"+
            ");";
    public static final String CHECK_FOTOS=" CREATE TABLE IF NOT EXISTS user_images(\n" +
            "id_image serial PRIMARY KEY,\n" +
            "image_name VARCHAR(32) NOT NULL,\n" +
            "id_user INTEGER REFERENCES users,\n" +
            "image_date TIMESTAMP DEFAULT NOW()\n"+
            ");";

    public static final String INSERT_USER="INSERT INTO users(user_name,password_hash) " +
            "VALUES (?,?) RETURNING id_user;";
    public static final String INSERT_SESSION="INSERT INTO sessions(id_user,isActive,last_update) " +
            "VALUES (?,?,?) RETURNING id_session;";
}
