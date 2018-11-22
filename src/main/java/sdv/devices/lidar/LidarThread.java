package sdv.devices.lidar;

import io.scanse.sweep.SweepDevice;
import io.scanse.sweep.SweepSample;
import sdv.tools.boxes.LidarBox;

import java.util.List;
import java.util.Vector;

/**
 * LidarThread handles the scanning by the lidar in a seperate
 * thread than the rest of the program. Since the LiDAR and SLAM
 * is not compatible we need to enter each sample of the LiDAR scan.
 * This results in us entering each of the 1000 samples in one revolution,
 * fetching the distance point and place them in a int-array which represents
 * a full revolution (360 degrees) and sends this array to the StorageBox
 * for the SLAM-algorithm to use.
 *
 * @author Ole-martin Steinnes
 */
public class LidarThread extends Thread {

    // Lidar instance
    private SweepDevice sweepDevice;

    // StorageBox instance made for LiDAR
    private LidarBox lidarBox;

    // Number of expected samples per revolution
    private int sampleLimit;

    // Number of scans contained by the Vector<>
    private int ns;

    /**
     * Constructor of the LidarThread-class. Creates accepts instances
     * of SweepDevice, LidarBox and the samplelimit for one revolution.
     *
     * @param sweepDevice   LiDAR-instance
     * @param lidarBox      StorageBox-instance
     * @param sampleLimit   Limit of samples per revolution
     */
    public LidarThread(SweepDevice sweepDevice, LidarBox lidarBox, int sampleLimit) {
        this.sweepDevice = sweepDevice;
        this.lidarBox = lidarBox;
        this.sampleLimit = sampleLimit;
        this.ns = 0;
    }

    @Override
    public void run(){
        while(true){

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

                    // For each scan
                    for (int x = 0; x < ns; x++) {

                        int[] scan = scans.elementAt(x);

                        lidarBox.setValue(scan);


                    }
                }
            }

        }
    }

    /**
     * Stops the LiDAR-scan and stops the LiDAR's motor.
     */
    public void stopLidar() {
        sweepDevice.stopScanning();
        sweepDevice.setMotorSpeed(0);
    }

}
