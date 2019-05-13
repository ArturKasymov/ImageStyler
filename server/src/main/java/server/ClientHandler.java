package server;

import model.ClientInteractor;
import model.Interactor;
import model.database.entity.User;
import model.repositories.CryptoRepo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Scanner;

import static util.ServerCommand.*;

public class ClientHandler extends Thread{

    private final Socket currentSocket;
    private final ClientInteractor interactor=Interactor.getInstance();
    private final ServerManager serverManager;

    private final DataInputStream dis;
    private final DataOutputStream dos;

    private int currentUserID;

    public ClientHandler(ServerManager serverManager,Socket currentSocket, DataInputStream dis, DataOutputStream dos){
        this.serverManager=serverManager;
        this.currentSocket=currentSocket;
        this.dis=dis;
        this.dos=dos;
        this.currentUserID=-1;
    }

    @Override
    public void run() {
        String inputData;
        boolean isRunning=true;

        // TODO delete logs
        System.out.println("new client added");

        while (isRunning)
        {
            try {
                inputData = dis.readUTF();
                if(inputData.equals(CLOSE_CONNECTION)){
                    synchronized (dos){
                        dos.writeUTF(CLOSE_CONNECTION);
                        isRunning=false;
                    }
                }
                else parseClientInput(inputData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.dis.close();
            this.dos.close();
            this.currentSocket.close();
            if(currentUserID!=-1)serverManager.userOffline(currentUserID,this);
            // TODO delete logs
            System.out.println("client connection closed");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void parseClientInput(String input){
        Scanner sc= new Scanner(input);
        String command=sc.next();

        String username;
        String password;

        switch (command) {
            case LOGIN:
                username=sc.next();
                password=sc.next();

                User storedUser=interactor.getUser(username);
                if(storedUser==null||!CryptoRepo.checkPassword(password,storedUser.getPassword_hash())){
                    synchronized (dos){
                        try {
                            dos.writeUTF(LOGIN+" "+FAIL);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    currentUserID=storedUser.getId_user();
                    synchronized (dos){
                        try {
                            dos.writeUTF(LOGIN+" "+SUCCESS+" "+currentUserID+" "+storedUser.getUser_name());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    serverManager.userOnline(currentUserID, this);
                }
                break;
            case REGISTER:
                username=sc.next();
                password=sc.next();
                try {
                    currentUserID=interactor.insertUser(username,password);
                    synchronized (dos){
                        try {
                            dos.writeUTF(REGISTER+" "+SUCCESS+" "+currentUserID+" "+username);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    serverManager.userOnline(currentUserID,this);
                    break;
                } catch (SQLException e) {
                    e.printStackTrace();
                    currentUserID=-1;
                }
                synchronized (dos){
                    try {
                        dos.writeUTF(REGISTER+" "+FAIL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LOGOUT:
                serverManager.userOffline(currentUserID,this);
                currentUserID=-1;
                break;

            case INSERT_IMAGE:
                String imangeName=sc.next();
                long imageDate=sc.nextLong();
                //TODO styleID from database
                String style=sc.next();
                try {
                    byte[] imageSizeArray = new byte[4];
                    dis.read(imageSizeArray);
                    int size = ByteBuffer.wrap(imageSizeArray).asIntBuffer().get();
                    byte[] imageArray = new byte[size];
                    dis.read(imageArray);
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageArray));

                    int imageID=interactor.insertImage(imangeName,currentUserID,imageDate);

                    //TODO generate Image in asynk
                    //generate(imageID,currentUserID,style,image)

                    for(ClientHandler temp : serverManager.getUserSessions(currentUserID)){
                        temp.insertUserImage(imageID,imangeName,imageDate);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    public void insertUserImage(int imageID, String imageName,long imageDate){
        try {
            dos.writeUTF(INSERT_IMAGE+" "+SUCCESS+" "+imageID+" "+imageName+" "+imageDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
