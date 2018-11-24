package sdv.networking.slam;

import sdv.networking.TcpServer;

import java.io.IOException;

/**
 * Class that extends the TcpServer-class. Makes i possible to send a byte-array to the client.
 *
 * @author Ole-martin Steinnes
 */
public class SlamServer extends TcpServer {

    public SlamServer() {
        super();
    }

    @Override
    public void messageFromClient() {

    }

    @Override
    public void sendToClient(byte[] byteArray) {
        try {
            outToClient.write(byteArray, 0, byteArray.length);
        } catch (IOException e) {
            System.out.println("Socket failed to print SLAM-map to GUI");
        }
    }
}
