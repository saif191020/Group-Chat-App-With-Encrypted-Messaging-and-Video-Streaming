import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Server {
    final static int PORT = 2001;

    static ArrayList<Socket> chatClientList;
    static ArrayList<Socket> videoClientList;

    public static void main(String[] args) {
        chatClientList = new ArrayList<Socket>();
        videoClientList = new ArrayList<Socket>();
        int connectedClients = 0;
        ServerSocket chatServerSocket, videoServerSocket;
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter The Group Name");
        String groupName = scan.nextLine();
        scan.close();
        try {

            chatServerSocket = new ServerSocket(PORT);
            videoServerSocket = new ServerSocket(PORT + 1);
            new VideoServer(videoServerSocket).start();
            System.out.println("Socket Created with Port No: 2001 and Listening ...");

            while (true) {
                Socket client = chatServerSocket.accept();
                DataOutputStream dout = new DataOutputStream(client.getOutputStream());
                dout.writeUTF(groupName);
                connectedClients++;
                System.out.println("Accecpted new Client into the Server ");
                System.out.println("Total Number of Connected Client  :" + connectedClients);
                Server.chatClientList.add(client);
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
            DataInputStream din = new DataInputStream(s.getInputStream());
            while (true) {
                String str = din.readUTF();
                if (str.startsWith("END")) {
                    s.close();
                    break;
                }
                for (Socket s : Server.chatClientList) {
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(str);
                }
            }
            int i = Server.chatClientList.indexOf(s);
            Server.chatClientList.remove(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class VideoServer extends Thread {

    ServerSocket videoServerSocket;

    VideoServer(ServerSocket ss) {
        videoServerSocket = ss;
    }

    public void run() {

        while (true) {
            try {
                Socket socket = videoServerSocket.accept();
                Server.videoClientList.add(socket);
                new VideoStreamThread(socket).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

class VideoStreamThread extends Thread {
    Socket s;

    VideoStreamThread(Socket socket) {
        s = socket;
    }

    public void run() {
        try {
            ImageIcon ic;
            ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
            while (true) {
                ic = (ImageIcon) oin.readObject();
                if (ic != null && ic.getDescription() != null && ic.getDescription().equals("END")) {
                    System.out.println("end recevied");
                    s.close();
                    break;
                } else {

                    for (Socket c : Server.videoClientList) {
                        //if(c==s) continue;
                        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(c.getOutputStream()));
                        oout.writeObject(ic);
                        oout.flush();
                    }
                    if (ic != null && ic.getDescription() != null && ic.getDescription().equals("END_VIDEO")) {
                        oin = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = Server.videoClientList.indexOf(s);
        Server.videoClientList.remove(i);
    }
}
