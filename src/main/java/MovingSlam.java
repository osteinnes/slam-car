import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.Laser;
import edu.wlu.cs.levy.breezyslam.components.PoseChange;
import edu.wlu.cs.levy.breezyslam.components.Position;
import edu.wlu.cs.levy.breezyslam.robots.WheeledRobot;
import io.scanse.sweep.SweepDevice;
import io.scanse.sweep.SweepSample;
import sdv.algorithms.slam.Robot;
import sdv.devices.motor.MotorController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Test-class that test SLAM with a moving car that gives us encoder-data as odometry.
 *
 * @author Ole-martin Steinnes
 */
public class MovingSlam {

    private static int MAP_SIZE_PIXELS = 820;

    static int coords2index(double x, double y) {
        return (int) (y * MAP_SIZE_PIXELS + x);
    }

    public static void main(String[] args) {
        MotorController motorController = new MotorController();


        motorController.start();


        System.out.println("Past motorcontroller.run()");

        // Timer.
        long time = System.currentTimeMillis();

        System.out.println("Before try catch");
        try (SweepDevice device = new SweepDevice("/dev/ttyUSB0")) {
            System.out.println("In try");
            int speed = device.getMotorSpeed();
            int rate = device.getSampleRate();
            int HOLE_WIDTH_MM = 200;
            int ns = 0;

            device.setMotorSpeed(1);
            device.setSampleRate(1000);

            System.out.println(String.format("Motor Speed: %s Hz", speed));
            System.out.println(String.format("Sample Rate: %s Hz", rate));

            device.startScanning();

            Laser myLidar;
            RMHCSLAM slam;
            PoseChange poseChange;
            WheeledRobot wheeledRobot;
            Robot robot;

            robot = new Robot(120, 170);
            poseChange = new PoseChange();

            myLidar = new Laser(1060, 1000,
                    360, 1,
                    1, 0.1);


            slam = new RMHCSLAM(myLidar, 820, 40, HOLE_WIDTH_MM);

            Position position = slam.getpos();
            System.out.println("Position: " + position);
            for (List<SweepSample> s : device.scans()) {
                //System.err.println(s);

                SweepSample sample = s.get(0);

                int distance = sample.getDistance();

                //System.out.println(s.size());

                int[] distanceA = new int[1060];

                Vector<int[]> scans = new Vector<int[]>();

                if (s.size() > 1059) {
                    //System.out.println(s.size());
                    for (int i = 0; i <= 1059; i++) {
                        int dist = s.get(i).getDistance();
                        //  System.out.println("Dist(i): " + dist);
                        distanceA[i] = dist * 10;
                        //System.out.println("DistA: " + distanceA[i]);
                    }
                    //  System.out.println(distanceA);
                    //  scans.add(distanceA);
                    scans.addElement(distanceA);
                    ns = scans.size();
                    //System.out.println("Scan size: " + ns);

                    // Encoder one ande two from motor controller.
                    int enc1, enc2;

                    // String array that holds encoder values.
                    String[] strings = motorController.getEncoder();
                    System.out.println("Before IF");
                    System.out.println("Strings: " + strings[1]);
                    // If strings do not contain "no response" we know strings contain
                    // proper encoder values. Hence, we assign them to PoseChange-object.
                    if (!strings[1].equalsIgnoreCase("no response")) {
                        System.out.println("In IF");

                        // encoder values
                        String encoder1 = strings[1];
                        String encoder2 = strings[3];

                        System.out.println("SLAMMM:: " + "ENKODER1: " +encoder1  + " -- ENKODER2: " +encoder2);


                        // Parsing encoder values from String to int.
                        enc1 = Integer.parseInt(encoder1);
                        enc2 = Integer.parseInt(encoder2);

                        System.out.println("PARSE:: " + "ENKODER1: " +enc1  + " -- ENKODER2: " +enc2);



                        // Computing PoseChange through abstract Robot-class.
                        poseChange = robot.computePoseChange(time, enc1 , enc2 );
                        System.out.println(poseChange.toString());
                        System.out.println("1:: " + poseChange.getDxyMm());
                    }

                    for (int x = 0; x < ns; x++) {
                        int[] scan = scans.elementAt(x);
                        //System.out.println("Element in scan: " + Arrays.toString(scan));
                        //System.out.println("dxy_mm: " + poseChange.getDxyMm() + " dtSeconds: " + poseChange.getDtSeconds() + " thetaDegrees: " + poseChange.getDthetaDegrees());
                        slam.update(scan, poseChange);
                        position = slam.getpos();
                        System.out.println("Position: " + position);
                        //System.out.println("Slam updated!");
                    }
                }

                if(time + 60000 < System.currentTimeMillis()) {

                    break;
                }
            }

            device.stopScanning();
            device.setMotorSpeed(0);

            byte[] mapbytes = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];

            slam.getmap(mapbytes);


            String filename = "TestMap20000.pgm";

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
}