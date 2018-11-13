package sdv.algorithms.slam;

import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.*;
import io.scanse.sweep.SweepDevice;
import io.scanse.sweep.SweepSample;
import sdv.networking.motor.SlamServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Interface for our SLAM
 *
 * @author Ole-martin Steinnes
 */
public class Slam {

    private Laser myLidar;
    private RMHCSLAM slam;
    private PoseChange poseChange;

    private SlamServer slamServer;

    private final int HOLE_WIDTH_MM = 200;

    private int ns = 0;
    private long time = System.currentTimeMillis();

    private static int MAP_SIZE_PIXELS = 820;

    static int coords2index(double x, double y) {
        return (int) (y * MAP_SIZE_PIXELS + x);
    }

    /**
     * Constructor of the SLAM-class
     */
    public Slam() {
        setUpSlam();

        slamServer = new SlamServer();
        slamServer.connect(8002);
    }

    /**
     * Sets up slam objects
     */
    private void setUpSlam() {
        // new position change object.
        poseChange = new PoseChange();

        // new LiDAR object.
        myLidar = new Laser(1060, 1000,
                360, 1,
                1, 0.1);

        // new SLAM library
        slam = new RMHCSLAM(myLidar, 820, 10, HOLE_WIDTH_MM);
    }

    /**
     * Updates the SLAM-algorithm based on lidar scans.
     * @param device LiDAR-object
     */
    public void updateSlam(SweepDevice device) {

        // Byte-array we store map in.
        byte[] mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];
        // Loops through samples of each scan
        for (List<SweepSample> s : device.scans()) {

            // Int-array of distances in scan.
            int[] distanceA = new int[1060];

            // Scan vector
            Vector<int[]> scans = new Vector<int[]>();

            // Enter when there is mor than 1059 samples in the scan.
            if (s.size() > 1059) {

                // For each sample, get distance.
                for (int i = 0; i <= 1059; i++) {
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
                System.out.println("Scan size: " + ns);

                // For each scan
                for (int x = 0; x < ns; x++) {

                    int[] scan = scans.elementAt(x);
                    // Update slam with new scan and position change
                    slam.update(scan, poseChange);

                    slam.getmap(mapbytes);
                    System.out.println("Sending mapbytes to server:");
                    System.out.println();
                    System.out.println(mapbytes.length);

                    System.out.println();
                    System.out.println(mapbytes[224550]);
                    slamServer.sendToClient(mapbytes);
                }
            }

            // Scan for 60 seconds then break
            if(time + 120000 < System.currentTimeMillis()) {

                break;
            }
        }
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
}
