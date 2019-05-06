package server;

import model.Interactor;
import model.database.provider.PostgreSQLDataProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static util.Constants.SERVER_ROOT_DIRECTORY;

public class ServerManager {

    private ServerSocket serverSocket;
    private Interactor interactor;

    public ServerManager(String dbname, String username,String password,String IP, int port){
        interactor = Interactor.createInstance(dbname,username,password,IP,port);
    }

    public void init(){
        checkServerRootDir();
        interactor.checkDataBase();
    }

    private void checkServerRootDir(){
        File dir = new File(SERVER_ROOT_DIRECTORY);
        if (!dir.exists())dir.mkdirs();
    }

    public void startServer(int serverPort){
        try {
            serverSocket = new ServerSocket(serverPort);

            System.out.println("Server started");
            while (true)
            {
                Socket clientSocket = null;
                try
                {
                    clientSocket = serverSocket.accept();

                    //here could be logs

                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    ClientHandler clientHandler = new ClientHandler(clientSocket,dis,dos);

                    clientHandler.start();
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
