package sdv.algorithms.slam;

import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.*;
import io.scanse.sweep.SweepDevice;
import io.scanse.sweep.SweepSample;
import sdv.devices.motor.MotorInterface;
import sdv.networking.motor.SlamServer;

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

    private SlamServer slamServer;
    private Robot robot;

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
     * @param slamServer     Server for transmitting SLAM-map
     * @param lidarSpeed     Motor speed of LiDAR
     */
    public void initSlam(SweepDevice sweepDevice, SlamServer slamServer, int lidarSpeed) {

        this.slamActive = true;
        this.lidarSpeed = lidarSpeed;

        if (this.lidarSpeed == 1) {
            this.sampleLimit = 1060;
        } else if (this.lidarSpeed == 2) {
            this.sampleLimit = 513;
        } else {
            this.sampleLimit = 1060;
        }

        this.sweepDevice = sweepDevice;
        this.slamServer = slamServer;

        // new position change object.
        poseChange = new PoseChange();

        // new LiDAR object.
        myLidar = new Laser(this.sampleLimit, 1000,
                360, 1,
                1, 0.1);

        // new SLAM library
        slam = new RMHCSLAM(myLidar, 820, 40, HOLE_WIDTH_MM);

        robot = new Robot(30, 140);
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

                    // For each sample, get distance.
                    for (int i = 0; i <= (this.sampleLimit - 1); i++) {
                        int dist = s.get(i).getDistance();
                        //  System.out.println("Dist(i): " + dist);
                        distanceA[i] = dist * 10;
                        //System.out.println("DistA: " + distanceA[i]);
                    }
                    //  System.out.println(distanceA);
                    //  scans.add(distanceA);

                    // Add distance to scan vector
                    scans.addElement(distanceA);
                    ns = scans.size();
                    if (motorActive) {
                        // Encoder one ande two from motor controller.
                        int enc1, enc2;

                        // String array that holds encoder values.
                        String[] strings = motorInterface.fetchEncoderData();
                        // System.out.println("Before IF");
                        // System.out.println("Strings: " + strings[1]);
                        // If strings do not contain "no response" we know strings contain
                        // proper encoder values. Hence, we assign them to PoseChange-object.
                        if (!strings[1].equalsIgnoreCase("no response")) {
                            //   System.out.println("In IF");

                            // encoder values
                            String encoder1 = strings[1];
                            String encoder2 = strings[3];

                            System.out.println();
                            System.out.println();
                            System.out.println();
                            System.out.println("ENKODER1: " + encoder1 + " -- ENKODER2: " + encoder2);
                            System.out.println();


                            // Parsing encoder values from String to int.
                            enc1 = Integer.parseInt(encoder1);
                            enc2 = Integer.parseInt(encoder2);

                            //   System.out.println("TIME: " + time);


                            // Computing PoseChange through abstract Robot-class.
                            poseChange = robot.computePoseChange(((System.currentTimeMillis())) / 1000.0, enc1, enc2);
                            System.out.println(poseChange.toString());
                            System.out.println();
                        }
                        // System.out.println("1:: " + poseChange.getDxyMm());
                    }
                    // For each scan
                    for (int x = 0; x < ns; x++) {

                        int[] scan = scans.elementAt(x);

                        // Update slam with new scan and position change
                        slam.update(scan, poseChange);
                        slam.getmap(mapbytes);
                        slamServer.sendToClient(mapbytes);
                    }
                }
            }
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

    public void doAddMotorInterface(MotorInterface motorInterface) {
        this.motorInterface = motorInterface;
        motorActive = true;
    }

    public void shutDown() {
        motorActive = false;
    }

    public boolean getMotorActive() {
        return motorActive;
    }

    public void close() {
        slamActive = false;
        writeMap();
        sweepDevice.stopScanning();
        sweepDevice.setMotorSpeed(0);
    }
}
