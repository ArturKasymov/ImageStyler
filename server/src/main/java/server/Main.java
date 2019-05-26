package server;

import static util.Constants.*;

public class Main {

    static ServerManager serverManager;

    public static void run(String[] args) throws Exception{

        serverManager = new ServerManager(DEFAULT_DATABASE_NAME,DEFAULT_DB_USERNAME,DEFAULT_DB_PASSWORD,DEFAULT_DB_IP,DEFAULT_DB_PORT);

        serverManager.init();

        try{
            serverManager.startServer(DEFAULT_SERVER_PORT);
        }
        finally {
            serverManager.stopServer();
        }
    }

    public static void main(String[] args) throws Exception {
        run(args);
    }
}
