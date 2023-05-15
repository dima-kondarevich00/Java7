

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;




@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final String FRAME_TITLE = "Messenger";

    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;

    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    JButton sendButton;
    public InstantMessenger getMessenger() {
        return messenger;
    }
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;
    private InstantMessenger messenger;
    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;

    public MainFrame() {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth())/2,
                (kit.getScreenSize().height - getHeight())/2);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu chatMenu = new JMenu("Menu");

        Action logInAction = new AbstractAction("Log in") {

            public void actionPerformed(ActionEvent arg0) {
                String value = JOptionPane.showInputDialog(MainFrame.this, "Enter you're name",
                        "Enter", JOptionPane.QUESTION_MESSAGE);
                messenger.setSender(value);
            }
        };
        menuBar.add(chatMenu);
        chatMenu.add(logInAction);

        Action ShowUsersList = new AbstractAction("Show users list") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatDataBase DB = new ChatDataBase();
                ArrayList<DataBaseUser> users = DB.getUsers();
                String line = "";
                for(int i = 0; i < users.size(); i++){
                    line += users.get(i).getName() + " (" + users.get(i).getAddres() + ") " + "\n";
                }
                JOptionPane.showMessageDialog(MainFrame.this,
                        line, "Full users list!",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        chatMenu.add(ShowUsersList);

        Action addChatAction = new AbstractAction("Find the user") {

            public void actionPerformed(ActionEvent arg0) {
                String value = JOptionPane.showInputDialog(MainFrame.this, "Find the user",
                        "Find the user", JOptionPane.QUESTION_MESSAGE);

                DataBaseUser user;
                if (messenger.getDataBase().getUser(value) == null){
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "User "+ value + " does nor exist", "Error!",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }else{
                    user = messenger.getDataBase().getUser(value);
                    JOptionPane.showMessageDialog(MainFrame.this, user.getName() + " exist in database!",
                            "User "+ user.getName(),
                            JOptionPane.INFORMATION_MESSAGE);
                }

            }
        };

        chatMenu.add(addChatAction);

        Action openPrivateDialog = new AbstractAction("Direct messenge") {
            public void actionPerformed(ActionEvent arg0) {
                String value = JOptionPane.showInputDialog(MainFrame.this, "Who is resiver?",
                        "Find the user", JOptionPane.QUESTION_MESSAGE);
                DataBaseUser user;
                if (messenger.getDataBase().getUser(value) == null){
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "User "+ value + " does not exist", "Error!",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }else{
                    user = messenger.getDataBase().getUser(value);
                }
                DialogFrame dialogFrame = new DialogFrame(user, MainFrame.this);
            }
        };

        chatMenu.add(openPrivateDialog);

        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
        final JLabel labelFrom = new JLabel("From");
        final JLabel labelTo = new JLabel("Receiver");
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Message"));
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                messenger.getDataBase().openData();
                System.out.println("1221");
                messenger.sendMessage(textFieldTo.getText(),
                        textAreaOutgoing.getText());
            }
        });
        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()

                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(Alignment.BASELINE)

                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());
        messenger = new InstantMessenger(this);
    }

    public JTextArea getTextAreaOutgoing() {
        return textAreaOutgoing;
    }

    public int getServerPort() {
        return SERVER_PORT;
    }

    public JTextArea getTextAreaIncoming() {
        return textAreaIncoming;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

}
