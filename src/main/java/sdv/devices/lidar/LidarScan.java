package sdv.devices.lidar;

import io.scanse.sweep.SweepDevice;
import io.scanse.sweep.SweepSample;
import sdv.tools.boxes.LidarBox;

import java.util.List;
import java.util.Vector;

public class LidarScan extends Thread {

    private SweepDevice sweepDevice;
    private LidarBox lidarBox;
    private int sampleLimit;
    private int ns;


    public LidarScan(SweepDevice sweepDevice, LidarBox lidarBox, int sampleLimit) {
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

    public void stopLidar() {
        sweepDevice.stopScanning();
        sweepDevice.setMotorSpeed(0);
    }

}
