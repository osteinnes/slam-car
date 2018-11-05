package sdv.networking.motor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP-server to the GUI. Serves as a communication relay
 * for the GUI to control the motor controller.
 */
public class TcpServer {

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
    public boolean connect(){
        try {
            while(!connected) {
                ServerSocket welcomeSocket = new ServerSocket(8000);
                Socket connectionSocket = welcomeSocket.accept();
                inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                if(connectionSocket.isConnected()) {
                    System.out.println("Client connected at " +connectionSocket.getRemoteSocketAddress());
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
    public void messageFromClient(){
        try {
            clientRequest = inFromClient.readLine();
            if(clientRequest != null){
                clientString = clientRequest;
                System.out.println("Received: " + clientString);
            }
            else {
                clientString = "";
                System.out.println("NOTHING RECEIVED");
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}