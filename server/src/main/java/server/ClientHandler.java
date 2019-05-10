package server;

import model.ClientInteractor;
import model.Interactor;
import model.database.entity.Session;

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

/*
        switch (command) {
            case CLOSE_CONNECTION:

            case REGISTER_USER:
                try {
                    int userID=interactor.insertUser(commandScanner.next(),commandScanner.next());
                    Date currentDate= new Date();
                    int sessionID=interactor.insertSession(userID,currentDate);
                    this.currentSession=new Session(sessionID,userID,currentDate);
                    System.out.println(String.format("%s %d %d %s",REGISTER_USER_SUCCESS, sessionID,userID,currentDate));

                    dos.writeUTF(String.format("%s %d %d %s",REGISTER_USER_SUCCESS, sessionID,userID,currentDate.getTime()));
                } catch (SQLException e){
                    e.printStackTrace();
                    dos.writeUTF(REGISTER_USER_EXCEPTION);
                }
                break;
            default:
                dos.writeUTF("Invalid input");
                break;
        }
        */
    }
}
