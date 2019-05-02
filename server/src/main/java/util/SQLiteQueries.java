package util;

public class SQLiteQueries {
    public static final String CHECK_USERS="create table IF NOT EXISTS users (\n" +
            "id_user serial primary key,\n" +
            "user_name varchar(32) not null unique," +
            "password_hash varchar(32) not null" +
            ");";
    public static final String CHECK_SESSIONS="create table IF NOT EXISTS sessions (\n" +
            "id_session serial primary key,\n" +
            "id_user integer references users,\n" +
            "isActive boolean" +
            ");";
    public static final String CHECK_FOTOS=" CREATE TABLE IF NOT EXISTS user_images(\n" +
            "id_image serial PRIMARY KEY,\n" +
            "image_name VARCHAR(32) NOT NULL,\n" +
            "id_user INTEGER REFERENCES users,\n" +
            "image_date TIMESTAMP DEFAULT NOW(),\n"+
            ");";

}
