package sdv.networking.slam;

/**
 * SlamMapStream contains a TCP-server that accepts connections
 * on socket from GUI. Extends thread and it's purpose is to
 * upload the SLAM-map to the GUI.
 *
 * @author Ole-martin Steinnes
 */
public class SlamMapStream extends Thread {

    // A SLAM server that computes the SLAM-algorithm and can
    // transmit the SLAM-map through a TCP connection to GUI.
    private SlamServer slamServer;

    // Byte-array that contains the generated map.
    private byte[] mapbytes;

    // Boolean flag that is true if there is a new map to upload
    private boolean newMap;

    /**
     * SlamMapStream's constructor sets up the connection with the GUI and initiates
     * the fields needed for transfer of bytes.
     *
     * @param port          Port of socket used to communicate with GUI
     * @param mapSizePixels Size of the map in pizels.
     */
    public SlamMapStream(int port, int mapSizePixels) {
        setUpMapStream(port);
        setFields(mapSizePixels);

    }

    @Override
    public void run() {

        slamServer.sendToClient(mapbytes);
        System.out.println("Uploading new map!");
        System.out.println();
        newMap = false;


    }

    /**
     * Stops the map-stream (closes sockets of the SLAM-server)
     */
    public void stopMapStream(){
        slamServer.closeSocket();
    }

    /**
     * Sets a new bytemap to be uploaded.
     * @param map generated map of robots surroundings.
     */
    public void setByteMap(byte[] map) {
        mapbytes = map;
        newMap = true;
    }


    /**
     * Sets up the map stream by creating a new SlamServer-instance and connect
     * to a socket at port "port"
     *
     * @param port the server is to be connected to.
     */
    private void setUpMapStream(int port) {
        this.slamServer = new SlamServer();
        this.slamServer.connect(port);
    }

    /**
     * Sets the fields needed for the map-stream.
     *
     * @param MAP_SIZE_PIXELS Size of the generated map in pixels.
     */
    private void setFields(int MAP_SIZE_PIXELS) {
        // Byte-array we store map in.
        mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];
    }

}
