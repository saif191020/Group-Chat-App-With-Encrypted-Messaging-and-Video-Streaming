import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.image.BufferedImage;
//import javax.swing.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.*;
import java.awt.*;
import com.github.sarxos.webcam.*; // for geting webcam Videos 

class Client extends JFrame {
    static String IP_ADDRESS_STRING = "localhost";
    static int PORT = 2000;
    static String CURRENT_USER = "Client";
    static String PASSWORD = "1234"; // FOR TESTING PURPOSES
    static boolean isSetupDone;
    static boolean runCam;
    static Socket videoSocket;
    static Socket audioSocket;
    static JFrame videoFrame = new JFrame();
    static final int VIDEO_HEIGHT = 320, VIDEO_WIDTH = 240;
    static Encryption enc = new Encryption();
    static Decryption dec = new Decryption();
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Socket clientSocket;
    JLabel groupName;
    JButton send, fileSend, videoStream;
    JTextField msg;
    JPanel chat;
    JScrollPane scrollPane;
    JFileChooser jfc;

    static {
        loginInterface();
    }

    private static void loginInterface() {
        Client.isSetupDone = false;

        JLabel nameLabel, ipLabel, portLabel, passwordLabel;
        JTextField nameTextField, ipTextField, portTextField;
        JPasswordField passwordTextField;
        JButton connect;
        JFrame frame = new JFrame();
        frame.setTitle("Set-UP");

        nameLabel = new JLabel("         Name :");
        ipLabel = new JLabel("IP Address :");
        passwordLabel = new JLabel(" Password  :");
        portLabel = new JLabel("             Port :");
        nameTextField = new JTextField(15);
        ipTextField = new JTextField(15);
        portTextField = new JTextField(15);
        passwordTextField = new JPasswordField(15);
        connect = new JButton("Connect !");
        ipTextField.setText("localhost");
        portTextField.setText(PORT + "");

        Container contentPane = frame.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        contentPane.add(nameLabel);
        contentPane.add(nameTextField);
        contentPane.add(ipLabel);
        contentPane.add(ipTextField);
        contentPane.add(portLabel);
        contentPane.add(portTextField);
        contentPane.add(passwordLabel);
        contentPane.add(passwordTextField);
        contentPane.add(connect);

        // Name
        layout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, nameTextField, 5, SpringLayout.EAST, nameLabel);
        layout.putConstraint(SpringLayout.NORTH, nameTextField, 5, SpringLayout.NORTH, contentPane);
        // IP Address
        layout.putConstraint(SpringLayout.WEST, ipLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, ipLabel, 5, SpringLayout.SOUTH, nameTextField);
        layout.putConstraint(SpringLayout.WEST, ipTextField, 5, SpringLayout.EAST, ipLabel);
        layout.putConstraint(SpringLayout.NORTH, ipTextField, 5, SpringLayout.SOUTH, nameTextField);
        // Port
        layout.putConstraint(SpringLayout.WEST, portLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, portLabel, 5, SpringLayout.SOUTH, ipTextField);
        layout.putConstraint(SpringLayout.WEST, portTextField, 5, SpringLayout.EAST, portLabel);
        layout.putConstraint(SpringLayout.NORTH, portTextField, 5, SpringLayout.SOUTH, ipTextField);
        // Password
        layout.putConstraint(SpringLayout.WEST, passwordLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.SOUTH, portTextField);
        layout.putConstraint(SpringLayout.WEST, passwordTextField, 5, SpringLayout.EAST, passwordLabel);
        layout.putConstraint(SpringLayout.NORTH, passwordTextField, 5, SpringLayout.SOUTH, portTextField);
        // Connect Button
        layout.putConstraint(SpringLayout.WEST, connect, 5, SpringLayout.EAST, portLabel);
        layout.putConstraint(SpringLayout.NORTH, connect, 5, SpringLayout.SOUTH, passwordTextField);

        // Boundries
        layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, portTextField);
        layout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, connect);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        connect.addActionListener(
                e -> ConnectToServer(nameTextField, ipTextField, portTextField, passwordTextField, frame));
        passwordTextField.addActionListener(
                e -> ConnectToServer(nameTextField, ipTextField, portTextField, passwordTextField, frame));

    }

    private static void ConnectToServer(JTextField nameTextField, JTextField ipTextField, JTextField portTextField,
            JPasswordField passwordTextField, JFrame frame) {
        if (nameTextField.getText().toString().isBlank() || ipTextField.getText().toString().isBlank()
                || new String(passwordTextField.getPassword()).isBlank()
                || portTextField.getText().toString().isBlank()) {
            String tPass = ((new String(passwordTextField.getPassword())).isBlank()) ? " Password Field" : "";
            String tName = (nameTextField.getText().toString().isBlank()) ? "Name Field" : "";
            JOptionPane.showMessageDialog(null, tName + tPass + " cannot be Empty", "Note",
                    JOptionPane.INFORMATION_MESSAGE);

        } else {
            // System.out.println("Vrtified ...");
            CURRENT_USER = nameTextField.getText().toString();
            IP_ADDRESS_STRING = ipTextField.getText().toString();
            PORT = Integer.parseInt(portTextField.getText().toString());
            PASSWORD = new String(passwordTextField.getPassword());
            Client.isSetupDone = true;
            frame.dispose();
        }
    }

    Client() {
        super("Chat Window:Client");
        setLayout(new BorderLayout());
        setUI();
        setSize(400, 550);
        setVisible(true);
        setDefaultCloseOperation(3);

        listeners();

    }

    private void listeners() {
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (msg.getText() == null || msg.getText().toString().trim().length() == 0) {
                    } else {
                        String content = msg.getText().toString();
                        msg.setText("");
                        DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                        dout.writeUTF(Client.CURRENT_USER + ":::" + Client.enc.encrypt(content, Client.PASSWORD));
                    }
                } catch (Exception e1) {

                    e1.printStackTrace();
                }
            }
        });
        msg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (msg.getText() == null || msg.getText().toString().trim().length() == 0) {
                    } else {
                        String content = msg.getText().toString();
                        msg.setText("");
                        DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                        dout.writeUTF(Client.CURRENT_USER + ":::" + Client.enc.encrypt(content, Client.PASSWORD));
                    }
                } catch (Exception e1) {

                    e1.printStackTrace();
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream()); // sendign
                    dout.writeUTF("GRP_INFO" + ":::" + Client.CURRENT_USER + " left the Chat.");
                    dout.writeUTF("END");
                    ObjectOutputStream oout = new ObjectOutputStream(videoSocket.getOutputStream());
                    oout.writeObject(new ImageIcon("images\\endImage.png", "END"));

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        fileSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    jfc.showOpenDialog(null);
                    if (jfc.getSelectedFile() != null) {

                        /*
                         * //To send Text Data Alone String s1 = "FILE_TRANS:::"; while ((b = (byte)
                         * din.read()) != -1) { s1 += (char) b; } din.close(); DataOutputStream dout =
                         * new DataOutputStream(clientSocket.getOutputStream()); dout.writeUTF(s1 +
                         * ":::" + jfc.getSelectedFile().getName() + ":::" + Client.CURRENT_USER);
                         * 
                         * System.out.println(s1 + ":::" + jfc.getSelectedFile().getName() + ":::" +
                         * Client.CURRENT_USER);
                         */
                        /*
                         * DataInputStream din = new DataInputStream(new
                         * FileInputStream(jfc.getSelectedFile())); DataOutputStream dout = new
                         * DataOutputStream(clientSocket.getOutputStream());
                         */
                        File file = jfc.getSelectedFile();
                        FileInputStream fis = new FileInputStream(file.getPath());
                        int fileLen = (int) file.length();
                        String transferINFO = "FILE_TRANS:::" + file.getName() + ":::" + fileLen + ":::"
                                + Client.CURRENT_USER;
                        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                        dos.writeUTF(transferINFO);
                        byte b[] = new byte[fileLen];
                        fis.read(b, 0, b.length);
                        fis.close();
                        dos.write(b, 0, b.length);
                        dos.flush();
                        addMessages("GRP_INFO", "You Send A File");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        videoStream.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Webcam cam = Webcam.getDefault();
                Client.runCam = true;
                cam.setViewSize(new Dimension(Client.VIDEO_HEIGHT, Client.VIDEO_WIDTH));
                try {
                    ImageIcon ic = null;
                    BufferedImage br = null;
                    ObjectOutputStream vstream = new ObjectOutputStream(Client.videoSocket.getOutputStream());
                    cam.open();
                    new VideoOutstreamThread(ic, br, vstream, cam).start();
                    new AudioOutStreamThread().start();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                videoStreamStopUI();

            }
        });
    }

    void videoStreamStopUI() {
        JFrame stopFrame = new JFrame();
        stopFrame.setTitle("Pack()");
        stopFrame.setLayout(new FlowLayout());
        JButton stopButton = new JButton("Stop");
        stopFrame.add(stopButton);
        stopFrame.pack(); // calling the pack() method
        stopFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        stopFrame.setLocationRelativeTo(null);
        stopFrame.setVisible(true);
        stopButton.addActionListener(ae -> {
            Client.runCam = false;
            stopFrame.dispose();
        });
        stopFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                Client.runCam = false;
                stopFrame.dispose();
            }
        });

    }

    private void setUI() {
        // initila UI setup
        groupName = new JLabel("Connecting...");

        send = new JButton();
        fileSend = new JButton();
        videoStream = new JButton();
        videoStream.setIcon(new ImageIcon("images\\videoicon.png"));
        send.setIcon(new ImageIcon("images\\sendicon.png"));
        fileSend.setIcon(new ImageIcon("images\\ftpicon.png"));
        fileSend.setToolTipText("File Transfer");
        videoStream.setToolTipText("Video Stream");
        send.setToolTipText("Send");
        msg = new JTextField(25);
        chat = new JPanel();
        scrollPane = new JScrollPane(chat);
        jfc = new JFileChooser();

        // NORTH
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(top, BorderLayout.NORTH);
        top.add(groupName);

        // CENTER
        add(scrollPane, BorderLayout.CENTER);
        // chat.setLayout(new BoxLayout(chat , BoxLayout.Y_AXIS));
        // scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        chat.setLayout(new BorderLayout());

        // SOUTH
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel p2 = new JPanel(new BorderLayout());
        JPanel p3 = new JPanel(new BorderLayout());
        add(p1, BorderLayout.SOUTH);
        p1.add(p2, BorderLayout.CENTER);
        p1.add(send, BorderLayout.EAST);
        p1.setBorder(new EmptyBorder(10, 10, 10, 10));
        p2.add(p3, BorderLayout.CENTER);
        p2.add(fileSend, BorderLayout.EAST);
        p3.add(msg, BorderLayout.CENTER);
        p3.add(videoStream, BorderLayout.EAST);

    }

    private void handleFileTransfer(String fileName, String fileLen, String sender, DataInputStream din) {
        try {
            File directory = new File("FTP Recieved");
            if (!directory.exists())
                directory.mkdir();
            int len = Integer.parseInt(fileLen);
            FileOutputStream fout = new FileOutputStream("FTP Recieved\\" + fileName);
            byte bytes[] = new byte[len];
            din.readFully(bytes, 0, bytes.length);
            fout.write(bytes, 0, bytes.length);
            fout.flush();
            fout.close();
            addMessages("GRP_INFO", fileName + " recieved from " + sender);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addMessages(String user, String msg) {
        // Adds Msg in panel Format to add to a chat window
        Color textColor, bgColor;
        FlowLayout layout = new FlowLayout();
        JPanel row = new JPanel();
        JLabel content = new JLabel(msg);
        JLabel sender = new JLabel(user + "                        ");
        JLabel time = new JLabel(getTime()); // Change to Actual Time
        JPanel message = new RoundedPanel();

        if (user.equals("GRP_INFO")) {
            time.setVisible(false);
            sender.setVisible(false);
            layout.setAlignment(FlowLayout.CENTER);
            textColor = new Color(255, 255, 255);
            bgColor = new Color(110, 103, 103);
        } else if (user.equals(Client.CURRENT_USER)) {
            layout.setAlignment(FlowLayout.RIGHT);
            textColor = new Color(255, 255, 255);
            bgColor = new Color(0, 132, 255);
        } else {
            layout.setAlignment(FlowLayout.LEFT);
            textColor = new Color(0, 0, 0);
            bgColor = new Color(197, 197, 197);
        }

        row.setLayout(layout);
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
        sender.setFont(new Font("Helvitica", Font.BOLD, 11));
        content.setFont(new Font("Helvitica", Font.PLAIN, 12));
        time.setFont(new Font("Helvitica", Font.PLAIN, 10));
        message.setBorder(new EmptyBorder(10, 10, 10, 10));

        message.setBackground(bgColor);
        sender.setForeground(textColor);
        content.setForeground(textColor);
        time.setForeground(textColor);

        message.add(sender);
        message.add(content);
        message.add(time);
        row.add(message);
        chat.add(row, BorderLayout.NORTH); // Adds msg to chat layout
        // chat.revalidate();

        JPanel newChat = new JPanel();
        newChat.setLayout(new BorderLayout());
        chat.add(newChat, BorderLayout.CENTER);
        chat = newChat;
        chat.revalidate();

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
        JScrollBar vertica = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertica.getMaximum());

    }

    private String getTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(date);
    }

    public static void main(String[] args) {

        // System.out.println("Start");
        while (!Client.isSetupDone) {
            System.out.print("");
        }
        // Wait till u get all info

        Client client = new Client();

        try {
            client.clientSocket = new Socket(IP_ADDRESS_STRING, PORT);
            DataInputStream din = new DataInputStream(client.clientSocket.getInputStream());
            String groupName = din.readUTF();
            client.groupName.setText(groupName);
            DataOutputStream dout = new DataOutputStream(client.clientSocket.getOutputStream());

            // Verification
            String request = din.readUTF();
            if (request.startsWith("RequestSecretText")) {
                dout.writeUTF(enc.encrypt(Client.PASSWORD, Client.PASSWORD));
            } else {
                try {
                    String str = dec.decrypt(request, Client.PASSWORD);
                    if (!str.equals(Client.PASSWORD)) {
                        JOptionPane.showMessageDialog(client, "You Have entred Wrong Password", "Invalid Password",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    client.dispose();
                    JOptionPane.showMessageDialog(client, "You Have entred Wrong Password", "Invalid Password",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            new ClientVideoStreamThread().start();
            new ClientAudioStreamThread().start();
            dout.writeUTF("GRP_INFO" + ":::" + Client.CURRENT_USER + " joined the Chat.");
            while (true) {
                String response = din.readUTF();
                String[] str = response.split(":::");
                if (str[0].equals("FILE_TRANS")) {
                    client.handleFileTransfer(str[1], str[2], str[3], din);
                } else if (str[0].equals("GRP_INFO"))
                    client.addMessages(str[0], str[1]);
                else
                    client.addMessages(str[0], Client.dec.decrypt(str[1], Client.PASSWORD));
            }

        } catch (java.net.ConnectException e) {
            client.groupName.setText("FAILED !");
            JOptionPane.showMessageDialog(client, "Server doesn't exist : Invalid IP Address", "Server Not Found",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (java.io.EOFException e) {
            System.out.println("Ended");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientVideoStreamThread extends Thread {
    Socket videoSocket;

    public void run() {
        try {
            videoSocket = new Socket(Client.IP_ADDRESS_STRING, Client.PORT + 1);
            Client.videoSocket = videoSocket;

            JFrame videoFrame = Client.videoFrame;
            ImageIcon ic;
            JLabel videoFeed = new JLabel();
            // videoFrame.setLayout(null);
            videoFrame.setTitle("Client :" + Client.CURRENT_USER);
            videoFrame.add(videoFeed);
            videoFrame.setVisible(false);
            videoFrame.setSize(Client.VIDEO_HEIGHT, Client.VIDEO_WIDTH);
            videoFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            videoFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    videoFrame.setVisible(false);
                }
            });
            while (true) {
                ObjectInputStream oin = new ObjectInputStream((videoSocket.getInputStream()));
                ic = (ImageIcon) oin.readObject();
                videoFeed.setIcon(ic);
                if (!videoFrame.isVisible())
                    videoFrame.setVisible(true);
                if (ic != null && ic.getDescription() != null && ic.getDescription().equals("END_VIDEO")) {
                    videoFrame.setVisible(false);
                }
            }

        } catch (java.io.EOFException e) {
            System.out.println("Ended");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class VideoOutstreamThread extends Thread {
    ImageIcon ic;
    BufferedImage br;
    ObjectOutputStream stream;
    Webcam cam;

    VideoOutstreamThread(ImageIcon ic, BufferedImage br, ObjectOutputStream stream, Webcam cam) {
        this.ic = ic;
        this.br = br;
        this.stream = stream;
        this.cam = cam;
    }

    public void run() {
        try {
            while (Client.runCam) {
                br = cam.getImage();
                ic = new ImageIcon(br);
                stream.writeObject(ic);
                stream.flush();
            }
            ic = new ImageIcon("images\\endVideo.png", "END_VIDEO");
            stream.writeObject(ic);
            stream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cam.close();
    }
}

class ClientAudioStreamThread extends Thread {
    Socket audioSocket;
    ObjectInputStream ois;
    AudioFormat format;
    DataLine.Info info;
    SourceDataLine speakers;
    byte[] data;

    public void run() {
        try {
            audioSocket = new Socket(Client.IP_ADDRESS_STRING, Client.PORT + 2);
            Client.audioSocket = audioSocket;
            data = new byte[1024];
            format = new AudioFormat(48000.0f, 16, 2, true, false);
            info = new DataLine.Info(SourceDataLine.class, format);
            data = new byte[1024];

            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();
            ois = new ObjectInputStream(audioSocket.getInputStream());
            while (true) {
                int dsize = ois.read(data);
                if (dsize == 1024) {
                    speakers.write(data, 0, dsize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class AudioOutStreamThread extends Thread {
    private ObjectOutputStream oos;
    private AudioFormat format;
    private DataLine.Info info;
    private TargetDataLine microphone;
    private byte[] data;
    private int dsize;

    AudioOutStreamThread() {

    }

    public void run() {
        try {
            // Audio Stuff
            format = new AudioFormat(48000.0f, 16, 2, true, false);
            microphone = AudioSystem.getTargetDataLine(format);
            info = new DataLine.Info(TargetDataLine.class, format);
            data = new byte[1024];

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            oos = new ObjectOutputStream(Client.audioSocket.getOutputStream());
            // read and send part
            while (Client.runCam) {
                dsize = microphone.read(data, 0, data.length);
                oos.write(data, 0, dsize);
                oos.reset();
            }
            System.out.println("[ Client ] : Attempting to stop ");
            oos.write(data, 0, 512);
            oos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
        microphone.stop();
        microphone.close();
    }
}