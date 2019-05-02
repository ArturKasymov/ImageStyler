package server;

import database.provider.PostgreSQLDataProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static util.Constants.SERVER_ROOT_DIRECTORY;

public class ServerManager {

    private ServerSocket serverSocket;
    private PostgreSQLDataProvider provider;

    public ServerManager(String dbname, String username,String password,String IP, int port){
        provider= new PostgreSQLDataProvider(dbname,username,password,IP,port);
    }

    public void init(){
        checkServerRootDir();
        provider.checkTables();

    }

    private void checkServerRootDir(){
        File dir = new File(SERVER_ROOT_DIRECTORY);
        if (!dir.exists())dir.mkdirs();
    }

    public void startServer(int serverPort){
        try {
            serverSocket = new ServerSocket(serverPort);

            while (true)
            {
                Socket clientSocket = null;
                try
                {
                    clientSocket = serverSocket.accept();

                    //here could be logs

                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    //SessionHandler s = new SessionHandler()

                    //Thread t = new ClientHandler(s, dis, dos, SessionID++);

                    // Invoking the start() method
                    //t.start();

                }
                catch (Exception e){
                    clientSocket.close();
                    e.printStackTrace();
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() throws IOException {
        serverSocket.close();
    }

}