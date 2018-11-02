package lidar;

import sdv.sensors.lidar.Lidar;

public class LidarTest {

    public static void main(String[] args) {
        try {
            Lidar lidar = new Lidar("/dev/ttyUSB0");

            lidar.setLidarValues(1, 1000);
            lidar.startLidarScan();
            lidar.stopLidarScan();
            lidar.stopLidarMotor();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
