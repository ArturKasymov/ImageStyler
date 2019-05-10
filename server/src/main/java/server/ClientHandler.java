package server;

import model.ClientInteractor;
import model.Interactor;
import model.database.entity.Session;
import model.database.entity.User;
import model.repositories.CryptoRepo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.Format;
import java.util.Date;
import java.util.Scanner;

import static util.ServerCommand.*;

public class ClientHandler extends Thread{

    private final Socket currentSocket;
    private final ClientInteractor interactor=Interactor.getInstance();

    private final DataInputStream dis;
    private final DataOutputStream dos;

    private Session currentSession;

    public ClientHandler(Socket currentSocket, DataInputStream dis, DataOutputStream dos){
        this.currentSocket=currentSocket;
        this.dis=dis;
        this.dos=dos;
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
        try
        {

            this.dis.close();
            this.dos.close();
            this.currentSocket.close();
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
                    synchronized (dos){
                        try {
                            dos.writeUTF(LOGIN+" "+SUCCESS+" "+storedUser.getId_user()+" "+storedUser.getUser_name());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case REGISTER:
                username=sc.next();
                password=sc.next();
                try {
                    int userID=interactor.insertUser(username,password);
                    synchronized (dos){
                        try {
                            dos.writeUTF(REGISTER+" "+SUCCESS+" "+userID+" "+username);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                synchronized (dos){
                    try {
                        dos.writeUTF(REGISTER+" "+FAIL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
}
