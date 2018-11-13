import sdv.algorithms.slam.Slam;
import sdv.sensors.lidar.Lidar;

/**
 * Test-class that tests SLAMM while car is standing still.
 *
 * @author Ole-martin Steinnes
 */
public class StaticSlam {
    public static void main(String[] args) {


        Lidar lidar = new Lidar();
        lidar.doConnectLidar("/dev/ttyUSB0");
        lidar.setLidarValues(1, 1000);
        lidar.startLidarScan();
        Slam slam = new Slam();
        slam.updateSlam(lidar.getLidarDevice());
        lidar.stopLidarScan();
        lidar.stopLidarMotor();
        slam.writeMap();

    }
}