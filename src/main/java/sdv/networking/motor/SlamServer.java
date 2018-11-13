package sdv.networking.motor;

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

    }

}
