import sdv.algorithms.slam.Slam;
import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorController;
import sdv.sensors.lidar.Lidar;

/**
 * @author Ole-martin Steinnes
 */
public class AppManager {

    private MotorController motorController;
    private RunWebcamera runWebcamera;
    private Lidar lidar;
    private Slam slam;

    public AppManager() {
        doSetUpApp();
    }

    private void doSetUpApp() {
            this.motorController = new MotorController();
            this.runWebcamera = new RunWebcamera();
            this.lidar = new Lidar();
    }
}
