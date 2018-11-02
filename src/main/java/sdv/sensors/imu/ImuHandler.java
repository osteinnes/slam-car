package sdv.sensors.imu;

/**
 * ImuHandler-class handles an imu-sensor through an Arduino-device.
 *
 * @author Ole-martin Steinnes
 * @version 0.1
 */
public class ImuHandler {

    // Creates IMU and Arduino-client instances.
    private Imu imu;

    /**
     * Constructor of the ImuHandler-class. Creates IMU and Arduino objects.
     */
    public ImuHandler() {
        this.imu = new Imu();
    }

    /**
     * @return IMU-object.
     */
    public Imu getImu() {
        return imu;
    }
}
