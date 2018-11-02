package sdv.sensors.lidar;

import io.scanse.sweep.SweepDevice;

/**
 * Represents a LiDAR-sensor.
 *
 * @author Ole-martin Steinnes
 */
public class Lidar {

    private SweepDevice device;

    /**
     * Constructor of the Lidar-class
     * @param port port Lidar is connected to
     */
    public Lidar(String port) {
        this.device = new SweepDevice(port);
    }

    /**
     * Sets Lidars values for motor speed and sample rate
     * @param motorSpeed    Speed of Lidar motor
     * @param sampleRate    Sample rate of Lidar scan
     */
    public void setLidarValues(int motorSpeed, int sampleRate) {

        device.setMotorSpeed(motorSpeed);
        device.setSampleRate(sampleRate);

    }

    /**
     * Starts the Lidar scan
     */
    public void startLidarScan() {
        device.startScanning();
    }

    /**
     * Stop the Lidar scan
     */
    public void stopLidarScan() {
        device.stopScanning();
    }

    /**
     * Stop motor of Lidar-sensor
     */
    public void stopLidarMotor() {
        device.setMotorSpeed(0);
    }

    /**
     * Returns Lidar-device
     * @return Lidar-device
     */
    public SweepDevice getLidarDevice() {
        return device;
    }

}
