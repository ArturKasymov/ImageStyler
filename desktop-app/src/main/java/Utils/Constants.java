package Utils;

public class Constants {

    public static final String TITLE = "Hello UJ";
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final String APP_ROOT_DIRECTORY=System.getProperty("user.home")+"\\.ImageStyler";
    public static final String LOCAL_DATABASE_NAME="localDatabase.db";
    public static final int NUM_STYLE_IMAGES = 6;

    public static final int DEFAULT_SERVER_PORT=7777;
    public static final String DEFAULT_SERVER_IP ="35.197.39.104";

    public enum SORT_BY {
        NAME_ASC,
        NAME_DESC,
        DATE_ASC,
        DATE_DESC
    }
}
