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
        String received;
        String command;
        boolean isRunning=true;

        // TODO delete logs
        System.out.println("new client added");

        while (isRunning)
        {
            try {
                dos.writeUTF(WAITING_COMMANDS);

                received = dis.readUTF();

                //TODO delete logs
                System.out.println(received);

                Scanner commandScanner= new Scanner(received);
                command=commandScanner.next();
                switch (command) {
                    case CLOSE_CONNECTION:
                        isRunning=false;
                        break;
                    case REGISTER_USER:
                        try {
                            int userID=interactor.insertUser(commandScanner.next(),commandScanner.next());
                            Date currentDate= new Date();
                            int sessionID=interactor.insertSession(userID,currentDate);
                            this.currentSession=new Session(sessionID,userID,currentDate);
                            System.out.println(String.format("%s %d %d %s",REGISTER_USER_SUCCESS, sessionID,userID,currentDate));

                            dos.writeUTF(String.format("%s %d %d %s",REGISTER_USER_SUCCESS, sessionID,userID,currentDate));
                        } catch (SQLException e){
                            e.printStackTrace();
                            dos.writeUTF(REGISTER_USER_EXCEPTION);
                        }
                        break;
                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }
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
}
