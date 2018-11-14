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

    // Boolean parameters for entering while, and if.
    private boolean startMotorControlParam;
    private boolean startSlamParam;
    private boolean startWebCamParam;
    private boolean stopMotorControlParam;
    private boolean stopSlamParam;
    private boolean stopWebCamParam;
    private boolean controllerConnected;

    /**
     * Constructor of our application manager
     */
    public AppManager() {
        doSetUpApp();
        doCreateBooleanParam();
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
     * Creates boolean parameters for when the program should start certain features of the program.
     */
    private void doCreateBooleanParam() {
        this.startMotorControlParam = (this.controlMessage.equalsIgnoreCase("MOTORCONTROLLER:START") && !this.appRunning);
        this.startSlamParam = (this.controlMessage.equalsIgnoreCase("SLAM:START") && this.appRunning && !this.slamRunning);
        this.startWebCamParam = (this.controlMessage.equalsIgnoreCase("WEBCAM:START") && !this.camRunning);
        this.stopMotorControlParam = (this.controlMessage.equalsIgnoreCase("stop") && this.appRunning);
        this.stopSlamParam = (this.controlMessage.equalsIgnoreCase("stopslam") && this.appRunning && this.slamRunning);
        this.stopWebCamParam = (this.controlMessage.equalsIgnoreCase("stopcam") && this.appRunning && this.camRunning);
        this.controllerConnected = this.appController.isConnected();
    }

    /**
     * Main logic of our program
     */
    private void doProgramLogic() {
        do {

            do {

                this.controlMessage = this.appController.getControlMsg();

                System.out.println("DEBUG STRING: " + this.controlMessage);

                if (startMotorControlParam) {

                    doStartMotorController();

                } else if (startSlamParam) {

                    doStartSlam();

                } else if (startWebCamParam) {

                    doStartWebCam();

                } else if (stopMotorControlParam) {

                    doStopMotorController();

                } else if (stopSlamParam) {

                    doStopSlam();

                } else if (stopWebCamParam) {

                    doStopWebCam();

                }
            } while (controllerConnected);

            do {

                if (appRunning) {
                    if (slamRunning) {
                        doStopSlam();
                    }

                    if (camRunning) {
                        doStopWebCam();
                    }

                    doStopMotorController();

                    appRunning = false;
                }

                this.appController.doStartController();
            } while (!controllerConnected);

        } while (true);
    }

    /**
     * Starts the motor controller.
     */
    private void doStartMotorController() {
        this.motorInterface = new MotorInterface();
        this.motorInterface.start();
        this.appRunning = true;
    }

    /**
     * Start slam-algorithm
     */
    private void doStartSlam() {
        this.lidar.doConnectLidar("/dev/ttyUSB0");
        this.lidar.setLidarValues(1, 1000);
        this.lidar.startLidarScan();

        this.slamServer.connect(8002);
        this.slam.initSlam(motorInterface, lidar.getLidarDevice(), slamServer, 1);
        slam.start();

        this.slamRunning = true;
    }

    /**
     * Starts the webcamera transmission to GUI.
     */
    private void doStartWebCam() {
        this.runWebcamera.start();
        this.camRunning = true;
    }

    /**
     * Stops the motor controller
     */
    private void doStopMotorController() {
        motorInterface.doStop();
        this.appRunning = false;
    }

    /**
     * Stops the SLAM-algorithm
     */
    private void doStopSlam() {
        slam.interrupt();
        this.slamRunning = false;
    }

    /**
     * Stops the webcam transmission to GUI.
     */
    private void doStopWebCam() {
        runWebcamera.interrupt();
        this.camRunning = false;
    }
}
