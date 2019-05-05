package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SessionHandler extends Thread{

    final Socket currentSocket;
    private long sessionID;

    final DataInputStream dis;
    final DataOutputStream dos;


    public SessionHandler(Socket currentSocket, DataInputStream dis, DataOutputStream dos){
        this.currentSocket=currentSocket;
        this.dis=dis;
        this.dos=dos;
    }



    @Override
    public void run() {
        String received;
        String toreturn;

        // TODO runtime logs
        System.out.println("new client added");

        while (true)
        {
            try {
                dos.writeUTF("WRITE COMMAND");

                // receive the answer from client
                received = dis.readUTF();

                if(received.equals("Exit"))
                {
                    this.currentSocket.close();
                    break;
                }


                switch (received) {
                    case "GET CURRENT SESSION ID" :
                        toreturn = String.valueOf(sessionID);
                        dos.writeUTF(toreturn);
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
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
