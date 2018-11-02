import sdv.algorithms.slam.Slam;
import sdv.sensors.lidar.Lidar;

/**
 * Test class for the position change input of SLAM-library
 *
 * @author Ole-martin Steinnes
 */
public class PositionChangeTest {

    public static void main(String[] args){

        Slam slam = new Slam();
        Lidar lidar = new Lidar("/dev/ttyUSB0");

        lidar.setLidarValues(1,100);
        lidar.startLidarScan();

        slam.updateSlam(lidar.getLidarDevice());

        lidar.stopLidarScan();
        lidar.stopLidarMotor();

         slam.writeMap();
        }
    }
