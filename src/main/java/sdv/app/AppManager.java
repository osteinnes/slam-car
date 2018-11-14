package sdv.app;


import sdv.algorithms.slam.Slam;
import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorInterface;
import sdv.networking.motor.SlamServer;
import sdv.sensors.lidar.Lidar;

/**
 * This application was designed as a project in Real-time programming at NTNU Ålesund.
 * Participants of the project are as follows; Ole-martin H. Steinnes, Sivert Løken,
 * Eirik G. Gustafsson, and Vebjørn Bjørlo-Larsen. Instructors in the Real-time programming
 * course is Ivar Blindheim and Webjørn Rekdalsbakken.
 *
 * Application Manager of our application. Waits for a GUI-controller to connect.
 * When the controller has connected it can decide to run some or all features
 * of the program. Features contained in this program are "MotorController",
 * "SLAM", and "Webcam". The "MotorController" is two threads that communicate with
 * the RoboClaw-card equipped on the car. The GUI sends commands such as, turnLeft,
 * turnRight, forward, and backwards to control the car. Furthermore, this Java-server
 * sends the RoboClaw request for encoder-data which is in return used in the programs
 * SLAM-algorithm to determine its location and map relative to that position.
 *
 * The Webcam-aspect of the program runs a webcam mounted on the car, and broadcasts this
 * to the GUI providing a real-time feed to the user controlling the device.
 *
 * While there is no autonomous-algorithm implemented in this program right now, it has been
 * designed with that in mind. The intended algorithm was D*, which would take the
 * programs SLAM-map, and current position, and wanted position as inputs. Furthermore,
 * it would determine where it could travel, and where it could not from the SLAM-map.
 *
 * @author Ole-martin Steinnes
 */
public class AppManager {

    // Objects used in the main program

    // Controller of the application (GUI)
    private AppController appController;

    // Interface between the GUI, main program and MotorController.
    // For motor controls and encoder data.
    private MotorInterface motorInterface;

    // A SLAM server that computes the SLAM-algorithm and can
    // transmit the SLAM-map through a TCP connection to GUI.
    private SlamServer slamServer;

    // Representation of a Scanse LiDAR
    private Lidar lidar;

    // Representation of the SLAM-algorithm
    private Slam slam;

    // An object that runs the Webcam and transmits image to GUI
    // through UDP.
    private RunWebcamera runWebcamera;

    // Strings used in the main program
    private String controlMessage;

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