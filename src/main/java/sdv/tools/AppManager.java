package sdv.tools;

import sdv.algorithms.slam.Slam;
import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorController;
import sdv.networking.motor.SlamServer;
import sdv.sensors.lidar.Lidar;

/**
 * @author Ole-martin Steinnes
 */
public class AppManager {

    private MotorController motorController;
    private RunWebcamera runWebcamera;
    private Lidar lidar;
    private Slam slam;
    private boolean connected;
    private SlamServer slamServer;

    public AppManager() {
        doSetUpApp();
        doStartApp();
    }

    private void doSetUpApp() {
        this.motorController = new MotorController();
        this.runWebcamera = new RunWebcamera();
    }

    private void doStartApp() {
        runWebcamera.run();
        motorController.run();
    }

    private void doProgramLogic() {

        do {
            if (connected) {
                this.slamServer = new SlamServer();

            }
        } while (true);

    }
}
