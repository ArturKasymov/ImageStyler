package server;

import model.ClientInteractor;
import model.Interactor;
import model.database.entity.User;
import model.repositories.ConnectionCryptoRepo;
import model.repositories.CryptoRepo;
import model.repositories.RGBConverterRepo;
import util.Constants;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

import static util.Constants.KEY_GEN_LENGTH;
import static util.Constants.SERVER_ROOT_DIRECTORY;
import static util.ServerCommand.*;

public class ClientHandler extends Thread{

    private final Socket currentSocket;
    private final ClientInteractor interactor = Interactor.getInstance();
    private final ServerManager serverManager;

    private final DataInputStream dis;
    private final DataOutputStream dos;

    private int currentUserID;
    private ConnectionCryptoRepo connectionCryptoRepo;


    public ClientHandler(ServerManager serverManager,Socket currentSocket, DataInputStream dis, DataOutputStream dos){
        this.serverManager=serverManager;
        this.currentSocket=currentSocket;
        this.dis=dis;
        this.dos=dos;
        this.currentUserID=-1;
        try {
            connectionCryptoRepo=new ConnectionCryptoRepo(KEY_GEN_LENGTH);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentUserPath(){
        return SERVER_ROOT_DIRECTORY+"/."+currentUserID;
    }

    @Override
    public void run() {
        String inputData;
        boolean isRunning=true;

        // TODO delete logs
        System.out.println("new client added");
        connectionCryptoRepo.sendPublicKey(dos);


        while (isRunning)
        {
            try {
                inputData = dis.readUTF();
                if(inputData.equals(INIT)){
                    connectionCryptoRepo.setClientPublicKey(dis);
                    System.out.println("Connection INIT");
                    connectionCryptoRepo.sendAESsettings(dos);
                    continue;
                }
                inputData=connectionCryptoRepo.decryptMessage(inputData);
                System.out.println(inputData);
                if(inputData.equals(CLOSE_CONNECTION)){
                    sendDataToClient(CLOSE_CONNECTION);
                    isRunning=false;
                }
                else parseClientInput(inputData);
            } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
                isRunning=false;
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
                username = sc.next();
                password = sc.next();
                User storedUser = interactor.getUser(username);
                if(storedUser == null || !CryptoRepo.checkPassword(password,storedUser.getPassword_hash())){
                    sendDataToClient(LOGIN + " " + FAIL);
                }
                else{
                    currentUserID=storedUser.getId_user();
                    interactor.checkUserDir(getCurrentUserPath());
                    String images =interactor.getUserImagesListString(currentUserID);
                    try {
                        byte [] encrypt=connectionCryptoRepo.encryptBytes(images.getBytes());
                        sendBytesToUser(LOGIN+" "+SUCCESS+" "+currentUserID+" "+
                                storedUser.getUser_name()+" "+encrypt.length,encrypt);
                        serverManager.userOnline(currentUserID, this);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REGISTER:
                username=sc.next();
                password=sc.next();
                try {
                    currentUserID=interactor.insertUser(username,password);
                    sendDataToClient(REGISTER+" "+SUCCESS+" "+currentUserID+" "+username);
                    interactor.checkUserDir(getCurrentUserPath());
                    serverManager.userOnline(currentUserID,this);
                    break;
                } catch (SQLException e) {
                    e.printStackTrace();
                    currentUserID=-1;
                }
                sendDataToClient(REGISTER+" "+FAIL);
                break;
            case LOGOUT:
                serverManager.userOffline(currentUserID,this);
                currentUserID=-1;
                break;
            case GET_IMAGE:
                int imageId = sc.nextInt();
                final String getImagePath=getCurrentUserPath()+"/."+imageId+".png";
                final int uID=currentUserID;
                serverManager.asyncTask(
                        ()-> {
                            try {
                                insertImageData(imageId,uID,ImageIO.read(new File(getImagePath)));
                            } catch (Exception e) {
                                sendDataToClient(INSERT_IMAGE_DATA+" "+FAIL);
                            }
                        });
                break;
            case INSERT_IMAGE:
                String imageName = sc.next();
                long imageDate = new Date().getTime();
                int styleID = sc.nextInt();
                String s_net = sc.next();
                Constants.NEURAL_NET net = Constants.NEURAL_NET.getItem(s_net);
                double d = sc.nextDouble();
                boolean preserveSize = sc.nextBoolean();
                int encryptImageSize= sc.nextInt();
                try {
                    BufferedImage image =ImageIO.read(new ByteArrayInputStream(
                            connectionCryptoRepo.decryptBytes(dis,encryptImageSize)));
                    int imageID = interactor.insertImage(imageName, currentUserID, imageDate);
                    final String imagePath = getCurrentUserPath()+"/."+imageID+".png";
                    serverManager.asyncTask(()->{
                        BufferedImage img = RGBConverterRepo.toBufferedImageOfType(image, 1);
                        BufferedImage generatedImage = interactor.generateImage(img, styleID, net, d, preserveSize);
                        try {
                            File imageFile = new File(imagePath);
                            OutputStream out;
                            out = new FileOutputStream(imageFile);
                            ImageIO.write(generatedImage, "png", out);
                        } catch (IOException e) { e.printStackTrace(); }

                        try{
                            if(currentUserID!=-1)
                                for(ClientHandler temp : serverManager.getUserSessions(currentUserID))
                                    temp.insertImageData(imageID,currentUserID, generatedImage);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    });
                        for(ClientHandler temp : serverManager.getUserSessions(currentUserID)){
                            temp.insertUserImage(imageID, currentUserID , imageName, imageDate);
                        }
                } catch (IOException | NullPointerException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
                break;
            case DELETE_IMAGE:
                int imageID = sc.nextInt();
                interactor.deleteUserImage(imageID, getCurrentUserPath());
                try {
                    for (ClientHandler temp : serverManager.getUserSessions(currentUserID)) {
                        temp.deleteLocalImage(imageID,currentUserID);
                    }
                } catch (NullPointerException e ){
                    e.printStackTrace();
                }
                break;
            case CHANGE_PASSWORD:
                String oldPassword = sc.next();
                String newPassword = sc.next();
                if (interactor.changePassword(currentUserID, oldPassword, newPassword)) {
                    for (ClientHandler temp : serverManager.getUserSessions(currentUserID)) {
                        if (temp!=this) temp.forceLogOut();
                        else sendDataToClient(CHANGE_PASSWORD + " " + SUCCESS);
                    }
                } else {
                    sendDataToClient(CHANGE_PASSWORD + " " + FAIL);
                }
                break;
        }
    }

    private void insertUserImage(int imageID,int currentUserID ,String imageName,long imageDate){
        sendDataToClient(INSERT_IMAGE+" "+SUCCESS+" "+imageID+" "+currentUserID+" "+imageName+" "+imageDate);
    }

    private void deleteLocalImage(int imageID,int currentUserID) {
        sendDataToClient(DELETE_IMAGE + " " + SUCCESS + " " + imageID+" "+currentUserID);
    }

    public void forceLogOut() {
        sendDataToClient(LOGOUT);
        serverManager.userOffline(currentUserID,this);
        currentUserID = -1;
    }

    private void sendBytesToUser(String data, byte [] bytes){
        synchronized (dos) {
            try {
                dos.writeUTF(connectionCryptoRepo.encryptMessage(data));
                dos.write(bytes);
                dos.flush();
            } catch (IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertImageData(int imageID, int userID, BufferedImage bufferedImage){
        synchronized (dos) {
            try {
                ByteArrayOutputStream generatedImage = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", generatedImage);
                byte [] encryptImage= connectionCryptoRepo.encryptBytes(generatedImage.toByteArray());
                dos.writeUTF(connectionCryptoRepo.encryptMessage(INSERT_IMAGE_DATA+" "+SUCCESS+" "+imageID+" "+userID+" "+encryptImage.length));
                dos.write(encryptImage);
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDataToClient(String data){
        synchronized (dos) {
            try {
                dos.writeUTF(connectionCryptoRepo.encryptMessage(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
