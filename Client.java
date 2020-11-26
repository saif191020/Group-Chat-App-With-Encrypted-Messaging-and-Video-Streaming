import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;  
import java.util.Date;  

//import javax.swing.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;

class Client extends JFrame {
    final static String IP_ADDRESS_STRING = "localhost";
    final static int PORT = 2001;
    Socket s;
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    JLabel groupName;
    JButton send;
    JTextField msg;
    JPanel chat;
    JScrollPane scrollPane;
    String currentUser = "Client";

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
                    if(msg.getText() == null || msg.getText().toString().trim().length() ==0){}
                    else{
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(currentUser + ":::" + msg.getText().toString());
                        msg.setText("");
                    }
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }
        });
        addWindowListener(new WindowListener(){
			public void windowDeactivated(WindowEvent we){}
			public void windowDeiconified(WindowEvent we){}
			public void windowIconified(WindowEvent we){}
			public void windowOpened(WindowEvent we){}
			public void windowActivated(WindowEvent we){}
			public void windowClosed(WindowEvent we){}

			public void windowClosing(WindowEvent we){
				try{
		 			DataOutputStream dout = new DataOutputStream(s.getOutputStream()); //sendign
                     dout.writeUTF("END");
		 		}catch(Exception e)
		 		{
		 			System.out.println(e);
		 		}
		 	}
		 });
    }

    private void setUI() {
        // initila UI setup
        groupName = new JLabel("GROUP NAME");

        send = new JButton("SEND");
        msg = new JTextField(25);
        chat = new JPanel();
        scrollPane = new JScrollPane(chat);

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
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        add(p, BorderLayout.SOUTH);
        p.add(msg, BorderLayout.CENTER);
        p.add(send, BorderLayout.EAST);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private void addMessages(String user, String msg) {
        // Adds Msg in panel Format to add to a chat window
        Color textColor, bgColor;
        FlowLayout layout = new FlowLayout();
        if (user.equals(currentUser)) {
            layout.setAlignment(FlowLayout.RIGHT);
            textColor = new Color(255,255,255);
            bgColor = new Color(0, 132, 255);
        } else {
            layout.setAlignment(FlowLayout.LEFT);
            textColor = new Color(0,0,0);
            bgColor = new Color(202, 189, 199);
        }

        JPanel row = new JPanel();
        JLabel content = new JLabel(msg);
        JLabel sender = new JLabel(user + "                        ");
        JLabel time = new JLabel(getTime()); //Change to Actual TIme 
        JPanel message = new JPanel();
        
        row.setLayout(layout);
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
        sender.setFont(new Font("Serif", Font.PLAIN, 10));
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
        Client client = new Client();
        Scanner scan = new Scanner(System.in);
        client.currentUser = scan.nextLine();
        scan.close();
        try {
            client.s = new Socket(IP_ADDRESS_STRING, PORT);
            DataInputStream din = new DataInputStream(client.s.getInputStream());
            String groupName =din.readUTF();
            client.groupName.setText(groupName);
            while (true) {
                String str[] = din.readUTF().split(":::");
                client.addMessages(str[0], str[1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}