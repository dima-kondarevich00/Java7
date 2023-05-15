

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class InstantMessenger {

    ArrayList<String> answers = new ArrayList<String>(10);
    private MainFrame frame;
    private DialogFrame dialogFrame;
    private String sender;

    public ChatDataBase getDataBase() {
        return dataBase;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private ChatDataBase dataBase = new ChatDataBase();
    public InstantMessenger(final MainFrame f) {
        this.frame = f;
        startServer();
    }

    public void sendMessage(DataBaseUser user, String message, DialogFrame frame){ // отправка
        this.dialogFrame = frame;
        try {
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Enter message text!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            final Socket socket = new Socket(user.getAddres(), this.frame.getServerPort());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(this.sender);
            out.writeUTF(message);
            out.writeUTF(user.getName());
            out.writeUTF("true");
            socket.close();
            frame.getTextAreaIn().append(sender + "(Me)" + " -> " + user.getAddres() + " "
                    + message + "\n");
            frame.getTextAreaOut().setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "We cannot find destination node",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Cannot send message", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendMessage(String destinationName, String message) {
        try {

            if (destinationName.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Enter destination node", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Enter message text!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            DataBaseUser temp = null;
            if (dataBase.getUser(destinationName)!=null){
                temp = dataBase.getUser(destinationName);
            }else {
                JOptionPane.showMessageDialog(frame,
                        "We cannot find the receiver!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            f+inal Socket socket = new Socket(temp.getAddres(), frame.getServerPort()); // приём
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(this.sender);
            out.writeUTF(message);
            out.writeUTF(temp.getName());
            out.writeUTF("false");
            socket.close();
            frame.getTextAreaIncoming().append(sender + "(Me)"+ " -> (" + temp.getAddres() + ") "
                    + message + "\n");
            frame.getTextAreaOutgoing().setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "We cannot find destination node",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Cannot send message", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startServer() {
        new Thread(new Runnable() {

            public void run() {
                try {
//					dataBase.openData();
                    final ServerSocket serverSocket = new ServerSocket(frame.getServerPort());
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(
                                socket.getInputStream());
                        final String senderName = in.readUTF();
                        final String message = in.readUTF();
                        final String name = in.readUTF();
                        final String flag = in.readUTF();
                        socket.close();

                        final String address = ((InetSocketAddress) socket
                                .getRemoteSocketAddress())
                                .getAddress()
                                .getHostAddress();
                        if (flag.equals("true")){
                            dialogFrame.getTextAreaIn().append(name + " (" + address + "): "
                                    + message + "\n");

                        }else{
                            frame.getTextAreaIncoming().append(name + " (" + address + "): "
                                    + message + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame,
                            "Server work error!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

}