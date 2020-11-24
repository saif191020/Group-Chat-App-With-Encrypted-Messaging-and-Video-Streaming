import javax.swing.*;
import javax.swing.border.EmptyBorder;


//import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
class Client extends JFrame{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    JLabel groupName;
    JButton send;
    JTextField msg;
    JPanel chat;
    JScrollPane scrollPane;
    String currentUser ="Client";

    Client() {
        setLayout(new BorderLayout());
        setUI();
        setSize(400, 550);
		setVisible(true);
        setDefaultCloseOperation(3);
         //TEMP FOR NOW WILL BE REMOVED
        msg.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                 System.out.println("CLICKED");
                JPanel p = addMessages(currentUser,msg.getText().toString());

                chat.add(p,BorderLayout.NORTH);
                //chat.revalidate();
                JPanel newChat = new JPanel();
                newChat.setLayout(new BorderLayout());
                chat.add(newChat,BorderLayout.CENTER);
                chat = newChat;
                chat.revalidate();
                msg.setText("");
            }
            
        });
        
    }



    private void setUI() {
        groupName= new JLabel("GROUP NAME");
        
        send = new JButton("SEND");
        msg = new JTextField(25);
        chat  = new JPanel();
        scrollPane = new JScrollPane(chat);

        //NORTH
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(top, BorderLayout.NORTH);
        top.add(groupName);
        
        //CENTER
        add(scrollPane,BorderLayout.CENTER);
        // chat.setLayout(new BoxLayout(chat , BoxLayout.Y_AXIS));
        //scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        chat.setLayout(new BorderLayout());

        
      
        //SOUTH 
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        add(p, BorderLayout.SOUTH);
        p.add(msg,BorderLayout.CENTER);
        p.add(send,BorderLayout.EAST);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private JPanel addMessages(String user,String msg){
        FlowLayout layout = new FlowLayout();
        if(!user.equals(currentUser)){
            layout.setAlignment(FlowLayout.RIGHT);
        }else{
            layout.setAlignment(FlowLayout.LEFT);
        }
        JPanel row = new JPanel();
        row.setLayout(layout);
        JLabel content =new JLabel(msg);
        JLabel sender =new JLabel(user+"                        ");
        sender.setFont(new Font("Serif", Font.PLAIN, 10));
        JLabel time =new JLabel("01:45");
        JPanel message =new JPanel();
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
        message.setBorder(new EmptyBorder(10, 10, 10, 10));
        message.add(sender);
        message.add(content);
        message.add(time);
        row.add(message);
        message.setBackground(Color.LIGHT_GRAY);
        return row;
    }


    public static void main(String[] args) {
        new Client();
    }
}