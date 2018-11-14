package sdv.networking.motor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP-server to the GUI. Serves as a communication relay
 * for the GUI to control the motor controller.
 *
 * @author Ole-martin Steinnes
 */
public abstract class TcpServer {

    // True when connected to GUI, false otherwise.
    public Boolean connected;

    // Message from client
    protected BufferedReader inFromClient;

    // Message to client
    protected DataOutputStream outToClient;

    // String from client
    protected String clientString;

    // Request from client
    protected String clientRequest;

    private ServerSocket welcomeSocket;

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
                welcomeSocket = new ServerSocket(port);
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

    public String getClientRequest() {
        return clientRequest;
    }

    public void closeSocket() {
        try {

            inFromClient.close();
            outToClient.close();
            connectionSocket.close();
            welcomeSocket.close();
            connected = false;
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}