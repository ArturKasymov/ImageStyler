package Utils;


public class Constants {

    public static final String separator = "/";

    public static final String TITLE = "Hello UJ";
    public static final int KEY_GEN_LENGTH = 2048;
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    public static final String APP_ROOT_DIRECTORY=System.getProperty("user.home")+separator+".ImageStyler";
    public static final String LOCAL_DATABASE_NAME="localDatabase.db";
    public static final int NUM_STYLE_IMAGES = 7;

    public static final int DEFAULT_SERVER_PORT=7777;
    public static final String DEFAULT_SERVER_IP = "35.197.39.104";

    public static final int MAX_LOGIN_LENGTH = 32;
    public static final int MAX_PASSWORD_LENGTH = 32;
    public static final int MAX_STRING_LENGTH = 32;

    public static final String IN_PROGRESS_IMAGE = separator+"TestImages"+separator+"in_progress.jpg";

    public enum SORT_BY {
        NAME_ASC,
        NAME_DESC,
        DATE_ASC,
        DATE_DESC
    }

    public enum NEURAL_NET {
        SQUEEZENET,
        VGG16,
        TRANSFORMER,
    }
}
