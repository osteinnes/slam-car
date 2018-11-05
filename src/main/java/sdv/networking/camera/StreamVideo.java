package sdv.networking.camera;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * Streams images trough UDP based DatagramSocket.
 *
 * @author: Eirik G. Gustafsson
 * @version: 25.09.2018.
 */
public class StreamVideo {
    // UDP DatagramSocket.
    private DatagramSocket socket;


    public StreamVideo(int localPort) {
        doSetupSocket(localPort);
    }

    /**
     * Readies the datagram socket.
     */
    private void doSetupSocket(int localPort) {
        try {
            this.socket = new DatagramSocket(localPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Created socket on port: " + localPort);
    }


    /***
     * Breaks down the image to bytes and sends it.
     *
     * @param image BufferedImage to be sent.
     * @throws IOException Something with flush method.
     */
    public void doSendImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();

        byte[] buffer = baos.toByteArray();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.socket.send(packet);
    }
}
