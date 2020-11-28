import javax.swing.*;
import javax.swing.border.EmptyBorder;



import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//import javax.swing.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;

class Client extends JFrame {
    static String IP_ADDRESS_STRING = "localhost";
    static int PORT = 2001;
    static String CURRENT_USER = "Client";
    static boolean isSetupDone;
    Socket s;
    /**
     *
     */
    private static final long serialVersionUID = 1L;
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
        JLabel nameLabel, ipLabel, portLabel;
        JTextField nameTextField, ipTextField, portTextField;
        JButton connect;
        JFrame frame = new JFrame();
        frame.setTitle("Set-UP");
        nameLabel = new JLabel("         Name :");
        ipLabel = new JLabel("IP Address :");
        portLabel = new JLabel("             Port :");
        nameTextField = new JTextField(15);
        ipTextField = new JTextField(15);
        portTextField = new JTextField(15);
        connect = new JButton("Connect !");
        ipTextField.setText("localhost");
        portTextField.setText("2001");
        Container contentPane = frame.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        contentPane.add(nameLabel);
        contentPane.add(nameTextField);
        contentPane.add(ipLabel);
        contentPane.add(ipTextField);
        contentPane.add(portLabel);
        contentPane.add(portTextField);
        contentPane.add(connect);

        layout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, nameTextField, 5, SpringLayout.EAST, nameLabel);
        layout.putConstraint(SpringLayout.NORTH, nameTextField, 5, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.WEST, ipLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, ipLabel, 5, SpringLayout.SOUTH, nameTextField);
        layout.putConstraint(SpringLayout.WEST, ipTextField, 5, SpringLayout.EAST, ipLabel);
        layout.putConstraint(SpringLayout.NORTH, ipTextField, 5, SpringLayout.SOUTH, nameTextField);

        layout.putConstraint(SpringLayout.WEST, portLabel, 5, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, portLabel, 5, SpringLayout.SOUTH, ipTextField);
        layout.putConstraint(SpringLayout.WEST, portTextField, 5, SpringLayout.EAST, portLabel);
        layout.putConstraint(SpringLayout.NORTH, portTextField, 5, SpringLayout.SOUTH, ipTextField);

        layout.putConstraint(SpringLayout.WEST, connect, 5, SpringLayout.EAST, portLabel);
        layout.putConstraint(SpringLayout.NORTH, connect, 5, SpringLayout.SOUTH, portTextField);

        layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, portTextField);
        layout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, connect);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((nameTextField.getText().toString().isBlank() || ipTextField.getText().toString().isBlank()
                        || portTextField.getText().toString().isBlank())) {
                    // Add msg
                    System.out.println("cancel");

                } else {
                    System.out.println("Varifird ...");
                    CURRENT_USER = nameTextField.getText().toString();
                    IP_ADDRESS_STRING = ipTextField.getText().toString();
                    PORT = Integer.parseInt(portTextField.getText().toString());
                    Client.isSetupDone = true;
                    frame.dispose();
                }

            }

        });
    }

    Client() {
        super("Chat Window");
        setLayout(new BorderLayout());
        setUI();
        setSize(400, 550);
        setVisible(true);
        setDefaultCloseOperation(3);
        // TEMP FOR NOW WILL BE REMOVED
        listeners();

    }

    private void listeners() {
        msg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("CLICKED");
                try {
                    if (msg.getText() == null || msg.getText().toString().trim().length() == 0) {
                    } else {
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(Client.CURRENT_USER + ":::" + msg.getText().toString());
                        msg.setText("");
                    }
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }
        });
        addWindowListener(new WindowListener() {
            public void windowDeactivated(WindowEvent we) {
            }

            public void windowDeiconified(WindowEvent we) {
            }

            public void windowIconified(WindowEvent we) {
            }

            public void windowOpened(WindowEvent we) {
            }

            public void windowActivated(WindowEvent we) {
            }

            public void windowClosed(WindowEvent we) {
            }

            public void windowClosing(WindowEvent we) {
                try {
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // sendign
                    dout.writeUTF("GRP_INFO" + ":::" + Client.CURRENT_USER + " left the Chat.");
                    dout.writeUTF("END");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        fileSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    jfc.showOpenDialog(null);

                    DataInputStream din = new DataInputStream(new FileInputStream(jfc.getSelectedFile()));
                    byte b;
                    String s1 = "FILE_TRANS:::";
                    while ((b = (byte) din.read()) != -1) {
                        s1 += (char) b;
                    }
                    din.close();
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(s1 + ":::" + jfc.getSelectedFile().getName() + ":::" + Client.CURRENT_USER);
                    System.out.println(s1 + ":::" + jfc.getSelectedFile().getName() + ":::" + Client.CURRENT_USER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void setUI() {
        // initila UI setup
        groupName = new JLabel("GROUP NAME");

        send = new JButton("SEND");
        fileSend = new JButton("File");
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
        add(p1, BorderLayout.SOUTH);
        p1.add(p2, BorderLayout.CENTER);
        p1.add(send, BorderLayout.EAST);
        p1.setBorder(new EmptyBorder(10, 10, 10, 10));
        p2.add(msg, BorderLayout.CENTER);
        p2.add(fileSend, BorderLayout.EAST);
    }

    private void handleFileTransfer(String fileContent, String fileName, String sender) {
        if (sender.equals(Client.CURRENT_USER)) {
            addMessages("GRP_INFO", "You sent a File ");
        } else {
            try {
                FileOutputStream fout = new FileOutputStream("FTP Recieved\\" + fileName);
                fout.write(fileContent.getBytes());
                fout.close();
                addMessages("GRP_INFO", fileName + " recieved from " + sender);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addMessages(String user, String msg) {
        // Adds Msg in panel Format to add to a chat window
        Color textColor, bgColor;
        FlowLayout layout = new FlowLayout();
        JPanel row = new JPanel();
        JLabel content = new JLabel(msg);
        JLabel sender = new JLabel(user + "                        ");
        JLabel time = new JLabel(getTime()); // Change to Actual TIme
        JPanel message = new JPanel();

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
        sender.setFont(new Font("Serif", Font.PLAIN, 12));
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
    }

    private String getTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        return formatter.format(date);
    }

    public static void main(String[] args) {

        System.out.println("Start");
        while (!Client.isSetupDone) {
            System.out.print("");
        }
        // Wait till u get all info

        Client client = new Client();
        // Scanner scan = new Scanner(System.in);
        // Client.CURRENT_USER = scan.nextLine();
        // scan.close();
        try {
            client.s = new Socket(IP_ADDRESS_STRING, PORT);
            DataInputStream din = new DataInputStream(client.s.getInputStream());
            String groupName = din.readUTF();
            client.groupName.setText(groupName);
            DataOutputStream dout = new DataOutputStream(client.s.getOutputStream());
            dout.writeUTF("GRP_INFO" + ":::" + Client.CURRENT_USER + " joined the Chat.");
            while (true) {
                String response = din.readUTF();
                String[] str =response.split(":::");
                if (str[0].equals("FILE_TRANS")) {
                    client.handleFileTransfer(str[1], str[2], str[3]);
                } else
                    client.addMessages(str[0], str[1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}