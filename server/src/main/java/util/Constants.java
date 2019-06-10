package util;

public class Constants {
    public static final int DEFAULT_SERVER_PORT=7777;
    public static final String DEFAULT_DB_IP="//localhost";
    public static final String DEFAULT_DB_USERNAME="postgres";
    public static final String DEFAULT_DB_PASSWORD="imagestyler";
    public static final String DEFAULT_DATABASE_NAME="postgres";
    public static final int DEFAULT_DB_PORT=5432;

    public static final String SERVER_ROOT_DIRECTORY=System.getProperty("user.home")+"/.ImageStyler";

    public static final int NUM_STYLE_IMAGES = 7;
    public static final int KEY_GEN_LENGTH = 8192;

    public enum NEURAL_NET {
        SQUEEZENET,
        VGG16,
        TRANSFORMER;

        public static NEURAL_NET getItem(String net) {
            switch (net) {
                case "SQUEEZENET":
                    return SQUEEZENET;
                case "VGG16":
                    return VGG16;
                case "TRANSFORMER":
                    return TRANSFORMER;
                default:
                    return SQUEEZENET;
            }
        }
    }
}
