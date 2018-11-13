package sdv.tools;


import sdv.algorithms.slam.Slam;
import sdv.devices.motor.MotorController;
import sdv.networking.motor.SlamServer;
import sdv.sensors.lidar.Lidar;

/**
 * @author Ole-martin Steinnes
 */
public class AppManager {

    private AppController appController;
    private String controlMessage;
    private MotorController motorController;
    private SlamServer slamServer;
    private Lidar lidar;
    private Slam slam;

    private boolean startExecuted;
    private boolean slamRunning;

    public AppManager() {
        doSetUpApp();
    }

    private void doSetUpApp() {
        this.appController = new AppController();
    }


    private void doProgramLogic() {
        do {
            this.controlMessage = this.appController.getControlMsg();

            if (this.controlMessage.equalsIgnoreCase("start") && !startExecuted) {

                this.motorController = new MotorController();
                this.motorController.run();
                this.startExecuted = true;

            } else if (this.controlMessage.equalsIgnoreCase("runslam") && this.startExecuted && !slamRunning) {

                this.slamServer = new SlamServer();
                this.lidar = new Lidar();

                this.lidar.doConnectLidar("/dev/ttyUSB0");
                this.lidar.setLidarValues(1, 1000);
                this.lidar.startLidarScan();

                Slam slam = new Slam(motorController, lidar.getLidarDevice(), slamServer, 1);
                slam.run();

            }
        } while (this.appController.isConnected());
    }
}
