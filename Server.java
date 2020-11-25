import java.net.*;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Server {
    final static int PORT = 2001;
    static ArrayList<Socket> clientList;

    public static void main(String[] args) {
        clientList = new ArrayList<Socket>();
        int connectedClients = 0;
        ServerSocket ss;
        try {
            ss = new ServerSocket(PORT);
            System.out.println("Socket Created with Port No: 2001 and Listening ...");
            while (true) {
                Socket client = ss.accept();
                connectedClients++;
                System.out.println("Accecpted new Client into the Server ");
                System.out.println("Total Number of Connected Client  :" + connectedClients);
                Server.clientList.add(client);
                new ClientListenThread(client).start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientListenThread extends Thread {
    Socket s;

    ClientListenThread(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            while (true) {
                DataInputStream din = new DataInputStream(s.getInputStream());
                String str = din.readUTF();
                if(str.startsWith("END")){
                    s.close();
                    break;
                }
                for (Socket s : Server.clientList) {
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(str);
                }
            }
            int i =Server.clientList.indexOf(s);
            Server.clientList.remove(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}