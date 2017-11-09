package chap15;
import java.io.*;
import java.net.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SimpleChatClient
{
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

	String host;
	int port;
    
    public void go() {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();
        
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        
        frame.setSize(650, 500);
        frame.setVisible(true);
        
    }
    
    private void setUpNetworking() {
        try {
            sock = new Socket(this.host,this.port);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(outgoing.getText());
                writer.flush();
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

	public SimpleChatClient(String host, int port) {
		this.host = host;
		this.port = port;
		
	}
	
    public static void main(String[] args) {
		int portToUse = VerySimpleChatServer.getPortNumFromArgs(args);
        new SimpleChatClient(args[0],portToUse).go();
    }
    
    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("client read " + message);
                    incoming.append(message + "\n");
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}

