package sdv.algorithms.slam;

import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.*;
import io.scanse.sweep.SweepDevice;
import io.scanse.sweep.SweepSample;
import sdv.devices.motor.MotorInterface;
import sdv.networking.slam.SlamMapStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Interface for our SLAM-algorithm
 *
 * @author Ole-martin Steinnes
 */
public class Slam extends Thread {

    private Laser myLidar;
    private RMHCSLAM slam;
    private PoseChange poseChange;

    private Robot robot;
    private SlamMapStream slamMapStream;

    private MotorInterface motorInterface;
    private SweepDevice sweepDevice;

    private int lidarSpeed;
    private int sampleLimit;

    private byte[] mapbytes;

    private final int HOLE_WIDTH_MM = 200;

    private int ns = 0;
    private long time = System.currentTimeMillis();

    private static int MAP_SIZE_PIXELS = 820;

    static int coords2index(double x, double y) {
        return (int) (y * MAP_SIZE_PIXELS + x);
    }

    private boolean motorActive;
    private boolean slamActive;

    /**
     * Constructor of the Slam-class.
     */
    public Slam() {
        setUpFields();

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
     * @param sweepDevice    Sweep LiDAR of the car
     * @param lidarSpeed     Motor speed of LiDAR
     */
    public void initSlam(SweepDevice sweepDevice, int lidarSpeed) {

        this.slamActive = true;
        this.lidarSpeed = lidarSpeed;

        this.slamMapStream = new SlamMapStream(8002, MAP_SIZE_PIXELS);
        this.slamMapStream.start();

        if (this.lidarSpeed == 1) {
            this.sampleLimit = 1060;
        } else if (this.lidarSpeed == 2) {
            this.sampleLimit = 530;
        } else {
            this.sampleLimit = 1060;
        }

        this.sweepDevice = sweepDevice;

        // new position change object.
        poseChange = new PoseChange();

        // new LiDAR object.
        myLidar = new Laser(this.sampleLimit, 1000,
                360, 1,
                1, 0.1);

        // new SLAM library
        slam = new RMHCSLAM(myLidar, 820, 30, HOLE_WIDTH_MM);

        robot = new Robot(30, 90);
    }


    /**
     * Updates the SLAM-algorithm based on lidar scans.
     */
    public void run() {

        do {
            // Loops through samples of each scan
            for (List<SweepSample> s : sweepDevice.scans()) {

                // Int-array of distances in scan.
                int[] distanceA = new int[this.sampleLimit];

                // Scan vector
                Vector<int[]> scans = new Vector<int[]>();


                // Enter when there is mor than 1059 samples in the scan.
                if (s.size() > (this.sampleLimit - 1)) {

                    System.out.println("Enterd IF with " + s.size() + " samples");

                    // For each sample, get distance.
                    for (int i = 0; i <= (this.sampleLimit - 1); i++) {
                        int dist = s.get(i).getDistance();

                        distanceA[i] = dist * 10;

                    }

                    // Add distance to scan vector
                    scans.addElement(distanceA);
                    ns = scans.size();
                    if (motorActive) {

                        // Encoder one ande two from motor controller.
                        int enc1, enc2;

                        System.out.println("About to fetch encoderdata");
                        System.out.println();

                        // String array that holds encoder values.
                        String[] strings = motorInterface.fetchEncoderData();

                        System.out.println("Fetched encoderdata");
                        System.out.println();

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
                            if (poseChange.getDtSeconds() > 2.0) {
                                System.out.println(poseChange.getDtSeconds());
                            }
                            System.out.println("PoseChange updated!");
                            System.out.println();
                        }
                    }
                    // For each scan
                    for (int x = 0; x < ns; x++) {

                        System.out.println("Before scans.elementAt(x)");
                        System.out.println();
                        int[] scan = scans.elementAt(x);
                        System.out.println("After scans.elementAt(x)");
                        System.out.println();

                        System.out.println("Slam updated!");
                        System.out.println();

                        // Update slam with new scan and position change
                        slam.update(scan, poseChange);
                        slam.getmap(mapbytes);
                        slamMapStream.setByteMap(mapbytes);
                    }
                }
            }

            System.out.println("slamActive: " + slamActive);

        } while (slamActive);
    }

    /**
     * Writes a map of the scan to a file.
     */
    public void writeMap() {
        // Byte-array we store map in.
        byte[] mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];

        slam.getmap(mapbytes);


        String filename = "SLAM-with-position-test.pgm";

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
     * Adds a motor interface to SLAM when there is a motor controller
     * active on the car. This is controller from the GUI, through the AppController
     * used in AppManager.
     *
     * @param motorInterface interface between GUI and Python-server.
     */
    public void doAddMotorInterface(MotorInterface motorInterface) {
        this.motorInterface = motorInterface;
        motorActive = true;
    }

    /**
     * Shuts down the MotorInterface-instance currently used when shutting down.
     */
    public void shutDown() {
        motorActive = false;
    }

    /**
     * Returns the motor controller status of the app. Whether it is active or not.
     * @return the motor controller status of the app. Whether it is active or not.
     */
    public boolean getMotorActive() {
        return motorActive;
    }

    /**
     * Sets SLAM inactive, writes SLAM-map, and stops LiDAR-scan.
     */
    public void close() {
        slamActive = false;
        slamMapStream.stopMapStream();
        slamMapStream.interrupt();
        writeMap();
        sweepDevice.stopScanning();
        sweepDevice.setMotorSpeed(0);
    }
}
