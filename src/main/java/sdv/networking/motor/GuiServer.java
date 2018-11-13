package sdv.networking.motor;

import java.io.IOException;
import java.net.SocketException;

/**
 * Class that extends a TcpServer-class. Makes it possible to receive from the client.
 *
 * @author Ole-martin Steinnes
 */
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
            }
        } catch (SocketException e) {

        } catch (IOException e) {
        }
    }

    @Override
    void sendToClient(byte[] byteArray) {

    }
}
