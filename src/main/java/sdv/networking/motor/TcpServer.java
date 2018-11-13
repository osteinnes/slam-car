package sdv.networking.motor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP-server to the GUI. Serves as a communication relay
 * for the GUI to control the motor controller.
 */
public abstract class TcpServer {

    // True when connected to GUI, false otherwise.
    public Boolean connected;
    // Message from client
    BufferedReader inFromClient;
    // Message to client
    DataOutputStream outToClient;
    // String from client
    public String clientString;
    // Request from client
    String clientRequest;

    private Socket connectionSocket;

    /**
     * Constructor of the TcpServer. Sets connected to false initially
     */
    public TcpServer() {
        connected = false;
    }

    /**
     * Listens to a socket at port 8000. Accepts a connection and reads client input.
     *
     * Returns true if connected, false otherwise.
     * @return true if connected, false otherwise.
     */
    public boolean connect(int port){
        try {
            while(!connected) {
                ServerSocket welcomeSocket = new ServerSocket(port);
                connectionSocket = welcomeSocket.accept();
                inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                if(connectionSocket.isConnected()) {
                    System.out.println("Client connected at " + connectionSocket.getRemoteSocketAddress());
                    connected = true;
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return connected;
    }

    /**
     * Recieves messages from client.
     */
    abstract void messageFromClient();

    /**
     * Sends messages to client.
     */
    abstract void sendToClient(byte[] byteArray);

    public String getClientString() {
        return clientString;
    }
}