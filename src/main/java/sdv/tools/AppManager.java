package sdv.tools;


import sdv.algorithms.slam.Slam;
import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorInterface;
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
    private MotorInterface motorInterface;
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

                System.out.println("DEBUG STRING: " + this.controlMessage);

                if (this.controlMessage.equalsIgnoreCase("MOTORCONTROLLER:START") && !this.appRunning) {

                    this.motorInterface = new MotorInterface();
                    this.motorInterface.start();
                    this.appRunning = true;

                } else if (this.controlMessage.equalsIgnoreCase("SLAM:START") && this.appRunning && !this.slamRunning) {

                    this.lidar.doConnectLidar("/dev/ttyUSB0");
                    this.lidar.setLidarValues(1, 1000);
                    this.lidar.startLidarScan();

                    this.slamServer.connect(8002);
                    this.slam.initSlam(motorInterface, lidar.getLidarDevice(), slamServer, 1);
                    slam.start();

                    this.slamRunning = true;

                } else if (this.controlMessage.equalsIgnoreCase("WEBCAM:START") && this.appRunning && !this.camRunning) {

                    this.runWebcamera.start();
                    this.camRunning = true;

                } else if (this.controlMessage.equalsIgnoreCase("stop") && this.appRunning) {
                    motorInterface.doStop();
                    this.appRunning = false;

                } else if (this.controlMessage.equalsIgnoreCase("stopslam") && this.appRunning && this.slamRunning) {

                    slam.interrupt();
                    this.slamRunning = false;

                } else if (this.controlMessage.equalsIgnoreCase("stopcam") && this.appRunning && this.camRunning) {

                    runWebcamera.interrupt();
                    this.camRunning = false;

                }
            } while (this.appController.isConnected());

            do {

                if (appRunning) {
                    if (slamRunning) {
                        slam.interrupt();
                        slamRunning = false;
                    }

                    if (camRunning) {
                        runWebcamera.interrupt();
                        camRunning = false;
                    }

                    motorInterface.doStop();

                    appRunning = false;
                }

                this.appController.doStartController();
            } while (!this.appController.isConnected());

        } while (true);
    }
}
