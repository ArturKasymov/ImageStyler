package server;

import model.ClientInteractor;
import model.Interactor;
import model.database.entity.User;
import model.repositories.CryptoRepo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Date;
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
                System.out.println(inputData);

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
        Scanner sc = new Scanner(input);
        String command = sc.next();

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
                    //TODO check user dir
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
                String imageName=sc.next();
                long imageDate=new Date().getTime();

                int styleID=sc.nextInt();

                try {
                    byte[] imageSizeArray = new byte[4];
                    dis.read(imageSizeArray);
                    int size = ByteBuffer.wrap(imageSizeArray).asIntBuffer().get();

                    byte[] imageArray = new byte[size];
                    dis.readFully(imageArray, 0, size);

                    //System.out.println(imageArray);

                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageArray));

                    int imageID = interactor.insertImage(imageName, currentUserID, imageDate);

                    serverManager.asyncTask(()->{
                        BufferedImage generatedImage=interactor.generateImage(image, styleID /*TODO handle arg*/, serverManager);
                        for(ClientHandler temp : serverManager.getUserSessions(currentUserID)){
                            temp.insertImageData(imageID, generatedImage);
                        }
                    });

                    for(ClientHandler temp : serverManager.getUserSessions(currentUserID)){
                        temp.insertUserImage(imageID, imageName, imageDate);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void insertUserImage(int imageID, String imageName,long imageDate){
        synchronized (dos) {
            try {
                dos.writeUTF(INSERT_IMAGE + " " + SUCCESS + " " + imageID + " " + imageName + " " + imageDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void insertImageData(int imageID, BufferedImage bufferedImage){

        synchronized (dos) {
            try {
                dos.writeUTF(INSERT_IMAGE_DATA + " " + SUCCESS + " " + imageID);

                ByteArrayOutputStream generatedImage = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", generatedImage);

                byte[] userImageSize = ByteBuffer.allocate(4).putInt(generatedImage.size()).array();
                dos.write(userImageSize);
                dos.write(generatedImage.toByteArray());
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
