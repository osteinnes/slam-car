package sdv.algorithms.slam;

import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.*;
import sdv.networking.slam.SlamMapStream;
import sdv.tools.boxes.EncoderBox;
import sdv.tools.boxes.LidarBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Interface for our SLAM-algorithm
 *
 * @author Ole-martin Steinnes
 */
public class Slam extends Thread {

    // Objects used for SLAM-algorithm
    private Laser myLidar;
    private RMHCSLAM slam;
    private PoseChange poseChange;
    private Robot robot;

    // Stream-thread that uploads SLAM-map to GUI.
    private SlamMapStream slamMapStream;

    // StorageBox objects
    private EncoderBox encoderBox;
    private LidarBox lidarBox;

    // Maximum amount of samples for one scan revolution
    private int sampleLimit;

    // Byte-array that stores last SLAM-generated map.
    private byte[] mapbytes;

    // Map size in pixels.
    private static int MAP_SIZE_PIXELS = 820;

    // Converts SLAM-coordinates to index in the map.
    static int coords2index(double x, double y) {
        return (int) (y * MAP_SIZE_PIXELS + x);
    }

    // Bool-flag that determines if the SLAM should be running or not.
    private boolean slamActive;

    // ExecutorService that administrates the uploading of Map.
    private ExecutorService executorService;

    /**
     * Constructor of the Slam-class.
     */
    public Slam(EncoderBox encoderBox, LidarBox lidarBox) {
        setUpFields();
        initSlam(encoderBox, lidarBox);
    }

    /**
     * Sets up fields used in our SLAM application
     */
    private void setUpFields() {
        // Byte-array we store map in.
        mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];
    }

    /**
     * Sets up objects used in our SLAM application
     *
     * @param lidarBox      StorageBox for lidar values
     * @param encoderBox    StorageBox for encoder values
     */
    public void initSlam(EncoderBox encoderBox, LidarBox lidarBox) {

        executorService = Executors.newFixedThreadPool(1);

        // Set fields
        this.slamActive = true;
        this.sampleLimit = 1000;

        // Created StorageBox-instances.
        this.encoderBox = encoderBox;
        this.lidarBox = lidarBox;

        // Start Map-stream thread.
        this.slamMapStream = new SlamMapStream(8002, MAP_SIZE_PIXELS);
        this.slamMapStream.start();

        // New objects for running SLAM.
        poseChange = new PoseChange();
        myLidar = new Laser(1000, 1000,
                360, 4000,
                0, 0.1);
        slam = new RMHCSLAM(myLidar, 820, 30, 1337);
        robot = new Robot(30, 110);
    }


    /**
     * Updates the SLAM-algorithm based on lidar scans.
     */
    public void run() {

        System.out.println("SLAM activated!");

        do {
            if (lidarBox.isReady()) {

                // Get last lidar-scan (one revolution)
                int[] scan = lidarBox.getValue();


                if (encoderBox.active()) {

                    // Encoder one and two from motor controller.
                    int enc1, enc2;

                    // String array that holds encoder values.
                    String[] strings = encoderBox.getValue();

                    // If strings do not contain "no response" we know strings contain
                    // proper encoder values. Hence, we assign them to PoseChange-object.
                    if (!strings[1].equalsIgnoreCase("no response")) {

                        // encoder values
                        String encoder1 = strings[1];
                        String encoder2 = strings[3];

                        // Parsing encoder values from String to int.
                        enc1 = Integer.parseInt(encoder1);
                        enc2 = Integer.parseInt(encoder2);

                        // Computing PoseChange through abstract Robot-class.
                        poseChange = robot.computePoseChange(((System.currentTimeMillis())) / 1000.0, enc1, enc2);
                    }
                }
                // Update slam with new scan and position change
                slam.update(scan, poseChange);
                slam.getmap(mapbytes);
                slamMapStream.setByteMap(mapbytes);
                executorService.execute(slamMapStream);
            }
        } while (slamActive);
    }

    /**
     * Writes a map of the scan to a file.
     */
    public void writeMap() {
        // Byte-array we store map in.
        byte[] mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];

        // Gets map from SLAM-algorithm
        slam.getmap(mapbytes);

        // Filename of the final map.
        String filename = "SLAM-map.pgm";

        System.out.println("\nSaving map to file " + filename);


        BufferedWriter output = null;

        try {
            FileWriter fstream = new FileWriter(filename);
            output = new BufferedWriter(fstream);
            output.write(String.format("P2\n%d %d 255\n", MAP_SIZE_PIXELS, MAP_SIZE_PIXELS));
            for (int y = 0; y < MAP_SIZE_PIXELS; y++) {
                for (int x = 0; x < MAP_SIZE_PIXELS; x++) {
                    // Output unsigned byte value
                    output.write(String.format("%d ", (int) mapbytes[coords2index(x, y)] & 0xFF));
                }
                output.write("\n");
            }
            output.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    /**
     * Sets SLAM inactive, writes SLAM-map, and stops LiDAR-scan.
     */
    public void close() {
        slamActive = false;
        slamMapStream.stopMapStream();
        executorService.shutdown();
        writeMap();
    }
}
