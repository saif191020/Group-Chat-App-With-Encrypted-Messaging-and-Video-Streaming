import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Server {
    final static int PORT = 2000;

    static ArrayList<Socket> chatClientList;
    static ArrayList<Socket> videoClientList;
    static ArrayList<ObjectOutputStream> audioClientList;
    static String ENCRYPTED_SECRET_STRING;

    public static void main(String[] args) {
        chatClientList = new ArrayList<Socket>();
        videoClientList = new ArrayList<Socket>();
        audioClientList = new ArrayList<ObjectOutputStream>();
        int connectedClients = 0;
        ServerSocket chatServerSocket, videoServerSocket, audioServerSocket;
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter The Group Name :");
        String groupName = scan.nextLine();
        scan.close();
        try {

            chatServerSocket = new ServerSocket(PORT);
            videoServerSocket = new ServerSocket(PORT + 1);
            audioServerSocket = new ServerSocket(PORT + 2);
            new VideoServer(videoServerSocket).start();
            new AudioServer(audioServerSocket).start();
            System.out.println("Server Created with Port No: 2000 and Listening ...");

            while (true) {
                Socket client = chatServerSocket.accept();
                DataOutputStream dout = new DataOutputStream(client.getOutputStream());
                dout.writeUTF(groupName);
                connectedClients++;
                if (connectedClients == 1) {
                    dout.writeUTF("RequestSecretText");
                    ENCRYPTED_SECRET_STRING = new DataInputStream(client.getInputStream()).readUTF();
                } else {
                    dout.writeUTF(ENCRYPTED_SECRET_STRING);
                }
                System.out.println("Accecpted new Client into the Server ");
                // System.out.println("Total Number of Connected Client :" + connectedClients);
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
                } else if (str.startsWith("FILE_TRANS")) {
                    byte bytes[] = new byte[Integer.parseInt(str.split(":::")[2])];
                    din.readFully(bytes, 0, bytes.length);
                    for (Socket ss : Server.chatClientList) {
                        if (ss == s)
                            continue;
                        DataOutputStream dout = new DataOutputStream(ss.getOutputStream());
                        dout.writeUTF(str);
                        dout.write(bytes, 0, bytes.length);
                        dout.flush();
                    }
                    continue;
                }
                for (Socket s : Server.chatClientList) {
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(str);
                }
            }
        } catch (SocketException e) {
            System.out.println("Person Disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = Server.chatClientList.indexOf(s);
        Server.chatClientList.remove(i);
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
            ObjectInputStream oin = new ObjectInputStream(s.getInputStream());
            while (true) {
                ic = (ImageIcon) oin.readObject();
                if (ic != null && ic.getDescription() != null && ic.getDescription().equals("END")) {
                    System.out.println("end recevied");
                    s.close();
                    break;
                } else {

                    for (Socket c : Server.videoClientList) {
                        // if(c==s) continue;
                        ObjectOutputStream oout = new ObjectOutputStream(c.getOutputStream());
                        oout.writeObject(ic);
                        oout.flush();
                    }
                    if (ic != null && ic.getDescription() != null && ic.getDescription().equals("END_VIDEO")) {
                        oin = new ObjectInputStream(s.getInputStream());
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

class AudioServer extends Thread {
    ServerSocket audioServerSocket;

    AudioServer(ServerSocket ss) {
        audioServerSocket = ss;
    }

    public void run() {
        try {
            while (true) {
                Socket s = audioServerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                Server.audioClientList.add(out);
                new AudioStreamThread(s, out).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class AudioStreamThread extends Thread {
    Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream out;

    AudioStreamThread(Socket s, ObjectOutputStream ot) {
        socket = s;
        out = ot;
    }

    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            byte[] data = new byte[1024];
            while (true) {
                int dsize = ois.read(data);
                if (dsize == 1024) {
                    for (ObjectOutputStream oout : Server.audioClientList) {
                        oout.write(data, 0, dsize);
                        oout.reset();
                    }
                } else if (dsize == 512) {
                    System.out.println("[ SERVER ] : dsize-" + dsize + " Client Stopped.");
                    ois = new ObjectInputStream(socket.getInputStream());
                }

            }
        } catch (SocketException e) {
            System.out.println("Person Disconnected");
        } catch (Exception e) {
            System.out.println(e);
        }
        int i = Server.audioClientList.indexOf(out);
        Server.audioClientList.remove(i);

    }

}