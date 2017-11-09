package chap15;
import java.io.*;
import java.net.*;
import java.util.*;



public class VerySimpleChatServer
{
	
	public static final int PORT=5000;

	private int portToUse;

	public VerySimpleChatServer(int portToUse) {
		this.portToUse = portToUse;
	}

	
	ArrayList<PrintWriter> clientOutputStreams;
	// WAS: 
    // ArrayList clientOutputStreams;
	
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        
        public ClientHandler(Socket clientSOcket) {
            try {
                sock = clientSOcket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
                
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

	

	public static int getPortNumFromArgs(String [] args, int whereToLook) {
		int portToUse = PORT; // use the default
		if (args.length > whereToLook) {
			try {
				portToUse = Integer.parseInt(args[whereToLook]);
			} catch (NumberFormatException nfe) {
				System.err.println("Could not convert " + args[whereToLook] +
								   "to a port number");
				System.exit(1);
			}
		}
		return portToUse;
	}
	
    public static void main(String[] args) {
		int portToUse = getPortNumFromArgs(args,0);
		System.out.println("Listening on port " + portToUse + "...");
        new VerySimpleChatServer(portToUse).go();
    }
    
    public void go() {
        clientOutputStreams = new ArrayList<PrintWriter>();
		// clientOutputStreams = new ArrayList();
				// WAS: new ArrayList();
        try {
            ServerSocket serverSock = new ServerSocket(this.portToUse);
            while(true) {
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
