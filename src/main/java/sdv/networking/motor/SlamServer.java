package sdv.networking.motor;

import java.io.IOException;

/**
 * Class that extends the TcpServer-class. Makes i possible to send a byte-array to the client.
 *
 * @author Ole-martin Steinnes
 */
public class SlamServer extends TcpServer{

    public SlamServer() {
        super();
    }

    @Override
    void messageFromClient() {

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
