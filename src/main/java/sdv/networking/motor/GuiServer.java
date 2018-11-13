package sdv.networking.motor;

import java.io.IOException;
import java.net.SocketException;

public class GuiServer extends TcpServer {

    public GuiServer() {
        super();
    }

    @Override
    public void messageFromClient() {
        try {
            clientRequest = inFromClient.readLine();
            if(clientRequest != null){
                clientString = clientRequest;
                //System.out.println("Received: " + clientString);
            }
            else {
                clientString = "";
                System.out.println("NOTHING RECEIVED");
            }
        } catch (SocketException e) {

        } catch (IOException e) {
        }
    }

    @Override
    void sendToClient(byte[] byteArray) {

    }
}
