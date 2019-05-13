package server;

import model.Interactor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static util.Constants.SERVER_ROOT_DIRECTORY;

public class ServerManager {

    private ServerSocket serverSocket;
    private Interactor interactor;
    private final Map<Integer, List<ClientHandler>> activeUsers;

    public ServerManager(String dbname, String username,String password,String IP, int port){
        interactor = Interactor.createInstance(dbname,username,password,IP,port);
        activeUsers=new HashMap<>();
    }

    public void userOnline(int UserID, ClientHandler handler){
        synchronized (activeUsers){
            if(!activeUsers.containsKey(UserID))activeUsers.put(UserID, new LinkedList<ClientHandler>(){});
            activeUsers.get(UserID).add(handler);
        }
    }

    public void userOffline(int UserID, ClientHandler handler){
        synchronized (activeUsers){
            activeUsers.get(UserID).remove(handler);
            //TODO add remove if empty
        }
    }

    public List<ClientHandler> getUserSessions(int UserID){
        synchronized (activeUsers){
            return activeUsers.get(UserID);
        }
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
                    ClientHandler clientHandler = new ClientHandler(this,clientSocket,dis,dos);

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
