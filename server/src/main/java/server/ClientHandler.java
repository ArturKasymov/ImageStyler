package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static util.ServerCommand.CLOSE_CONNECTION;

public class ClientHandler extends Thread{

    final Socket currentSocket;

    final DataInputStream dis;
    final DataOutputStream dos;


    public ClientHandler(Socket currentSocket, DataInputStream dis, DataOutputStream dos){
        this.currentSocket=currentSocket;
        this.dis=dis;
        this.dos=dos;
    }



    @Override
    public void run() {
        String received;
        String toreturn;
        String command;
        boolean isRunning=true;

        // TODO runtime logs
        System.out.println("new client added");

        while (isRunning)
        {
            try {
                //dos.writeUTF("WRITE COMMAND");

                // receive the answer from client

                received = dis.readUTF();

                Scanner commandScanner= new Scanner(received);
                command=commandScanner.next();

                switch (command) {
                    case CLOSE_CONNECTION:
                        isRunning=false;
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
            // TODO runtime logs
            System.out.println("client connection closed");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
