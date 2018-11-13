import sdv.algorithms.slam.Slam;
import sdv.devices.motor.MotorController;
import sdv.networking.motor.SlamServer;
import sdv.sensors.lidar.Lidar;

/**
 * Test-class that tests SLAMM while car is standing still.
 *
 * @author Ole-martin Steinnes
 */
public class StaticSlam {
    public static void main(String[] args) {

        MotorController motorController = new MotorController();
        SlamServer slamServer = new SlamServer();
        Lidar lidar = new Lidar();
        lidar.doConnectLidar("/dev/ttyUSB0");
        lidar.setLidarValues(1, 1000);
        lidar.startLidarScan();
        Slam slam = new Slam();
        slam.run();


    }
}