package server;

import model.Interactor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static util.Constants.NUM_STYLE_IMAGES;
import static util.Constants.SERVER_ROOT_DIRECTORY;

public class ServerManager {

    private ServerSocket serverSocket;
    private Interactor interactor;
    private final Map<Integer, List<ClientHandler>> activeUsers;

    private ScheduledExecutorService executor;

    // TODO: HANDLE
    public BufferedImage styleImages[] = new BufferedImage[NUM_STYLE_IMAGES];

    public ServerManager(String dbname, String username,String password,String IP, int port){
        interactor = Interactor.createInstance(dbname,username,password,IP,port);
        activeUsers = new HashMap<>();
        // TODO: HANDLE
        styleImages[0] = getImage("/TestImages/img1.png");
        styleImages[1] = getImage("/TestImages/la_muse.jpg");
        styleImages[2] = getImage("/TestImages/rain_princess.jpg");
        styleImages[3] = getImage("/TestImages/udnie.jpg");
        styleImages[4] = getImage("/TestImages/starry_night.jpg");
        styleImages[5] = getImage("/TestImages/tubingen.jpg");
    }

    // TODO: HANDLED FUNCTION
    private BufferedImage getImage(String path) {
        try {
            return ImageIO.read(ServerManager.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        executor = Executors.newScheduledThreadPool(5);
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

    public void asyncTask(Runnable task) {
        executor.execute(task);
    }

}
