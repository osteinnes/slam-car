package sdv.tools;


import sdv.algorithms.slam.Slam;
import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorController;
import sdv.networking.motor.SlamServer;
import sdv.sensors.lidar.Lidar;

/**
 * Application Manager of our application.
 *
 * @author Ole-martin Steinnes
 */
public class AppManager {

    // Objects used in the main program
    private AppController appController;
    private String controlMessage;
    private MotorController motorController;
    private SlamServer slamServer;
    private Lidar lidar;
    private Slam slam;
    private RunWebcamera runWebcamera;

    // Booleann flags used in main program
    private boolean appRunning;
    private boolean slamRunning;
    private boolean camRunning;

    /**
     * Constructor of our application manager
     */
    public AppManager() {
        doSetUpApp();
        doProgramLogic();
    }

    /**
     * Sets up objects for the appliication.
     */
    private void doSetUpApp() {
        this.appController = new AppController();
        this.slamServer = new SlamServer();
        this.lidar = new Lidar();
        this.slam = new Slam();
        this.runWebcamera = new RunWebcamera();
    }

    /**
     * Main logic of our program
     */
    private void doProgramLogic() {
        do {

            do {

                this.controlMessage = this.appController.getControlMsg();

                if (this.controlMessage.equalsIgnoreCase("start") && !this.appRunning) {

                    this.motorController = new MotorController();
                    this.motorController.start();
                    this.appRunning = true;

                } else if (this.controlMessage.equalsIgnoreCase("runslam") && this.appRunning && !this.slamRunning) {

                    this.lidar.doConnectLidar("/dev/ttyUSB0");
                    this.lidar.setLidarValues(1, 1000);
                    this.lidar.startLidarScan();

                    this.slamServer.connect(8002);
                    this.slam.initSlam(motorController, lidar.getLidarDevice(), slamServer, 1);
                    slam.start();

                    this.slamRunning = true;

                } else if (this.controlMessage.equalsIgnoreCase("runcam") && this.appRunning && !this.camRunning) {

                    this.runWebcamera.start();
                    this.camRunning = true;

                } else if (this.controlMessage.equalsIgnoreCase("stop") && this.appRunning) {
                    //TODO: Create stop capabilities of MotorController.
                } else if (this.controlMessage.equalsIgnoreCase("stopslam") && this.appRunning && this.slamRunning) {

                    slam.interrupt();

                } else if (this.controlMessage.equalsIgnoreCase("stopcam") && this.appRunning && this.camRunning) {

                    runWebcamera.interrupt();

                }
            } while (this.appController.isConnected());

            do {
                this.appController.doStartController();
            } while (!this.appController.isConnected());

        } while (true);
    }
}
