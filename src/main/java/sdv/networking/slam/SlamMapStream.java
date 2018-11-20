package sdv.networking.slam;

import sdv.networking.SlamServer;

/**
 * SlamMapStream contains a TCP-server that accepts connections
 * on socket from GUI. Extends thread and
 *
 * @author Ole-martin Steinnes
 */
public class SlamMapStream extends Thread {

    // A SLAM server that computes the SLAM-algorithm and can
    // transmit the SLAM-map through a TCP connection to GUI.
    private SlamServer slamServer;

    private byte[] mapbytes;

    private boolean newMap;

    public SlamMapStream(int port, int mapSizePixels) {
        setUpMapStream(port);
        setFields(mapSizePixels);

    }

    @Override
    public void run() {
        while (true) {
            if (newMap) {
                slamServer.sendToClient(mapbytes);
                System.out.println("Uploading new map!");
                System.out.println();
                newMap = false;
            }
        }
    }



    public void stopMapStream(){
        slamServer.closeSocket();
    }

    public void setByteMap(byte[] map) {
        mapbytes = map;
        newMap = true;
    }


    private void setUpMapStream(int port) {
        this.slamServer = new SlamServer();
        this.slamServer.connect(port);
    }

    private void setFields(int MAP_SIZE_PIXELS) {
        // Byte-array we store map in.
        mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];
    }

}
