package sdv.networking.motor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    public Boolean connected;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    public String clientString;
    String clientRequest;

    public TcpServer() {
        connected = false;
    }

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

    public void messageFromClient(){
        try {
            clientRequest = inFromClient.readLine();
            if(clientRequest != null){
                clientString = clientRequest;
                System.out.println("Received: " + clientString);
            }
            else {
                System.out.println("NOTHING RECEIVED");
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}