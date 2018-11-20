package sdv.app;


import javafx.application.Platform;
import sdv.algorithms.slam.Slam;
import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorInterface;
import sdv.networking.motor.SlamServer;
import sdv.sensors.lidar.Lidar;
import sdv.tools.StorageBox;

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


    // Represenation of Storage Box
    private StorageBox storageBox;

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
    private boolean disconnectController;

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
        this.storageBox = new StorageBox();
    }

    /**
     * Creates boolean parameters for when the program should start certain features of the program.
     */
    private void doCreateBooleanParam() {
        this.startMotorControlParam = (this.controlMessage.equalsIgnoreCase("MOTORCONTROLLER:START") && !this.appRunning);
        this.startSlamParam = (this.controlMessage.equalsIgnoreCase("SLAM:START") && !this.slamRunning);
        this.startWebCamParam = (this.controlMessage.equalsIgnoreCase("WEBCAM:START") && !this.camRunning);
        this.stopMotorControlParam = (this.controlMessage.equalsIgnoreCase("MOTORCONTROLLER:STOP") && this.appRunning);
        this.stopSlamParam = (this.controlMessage.equalsIgnoreCase("SLAM:STOP")&& this.slamRunning);
        this.stopWebCamParam = (this.controlMessage.equalsIgnoreCase("WEBCAM:STOP") && this.camRunning);
        this.disconnectController = (this.controlMessage.equalsIgnoreCase("STOP"));
        this.controllerConnected = this.appController.isConnected();
    }

    /**
     * Main logic of our program
     */
    private void doProgramLogic() {
        long k = System.currentTimeMillis();
        do {



            do {

                this.controlMessage = this.appController.getControlMsg();

                if ((k + 3000) < System.currentTimeMillis()) {
                    System.out.println(" Thread count: " + java.lang.Thread.activeCount());
                    k = System.currentTimeMillis();
                }

                doCreateBooleanParam();
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

                } else if (disconnectController) {
                    System.out.println("Disconnecting controllers.");
                    controllerConnected = false;
                }
            } while (controllerConnected);

            do {


                if (slamRunning) {
                    doStopSlam();
                    System.out.println("SLAM stopped");
                }

                if (camRunning) {
                    doStopWebCam();
                    System.out.println("Webcam stopped.");
                }

                if (appRunning) {
                    doStopMotorController();
                    System.out.println("Motor controller stopped.");
                }

                this.appController.closeController();
                System.out.println("AppController closed.");

                try {
                    Thread.currentThread().sleep(3000);
                    System.out.println("Delay 3 sec, restarting AppController");
                    this.appController = new AppController();
                    this.appController.doStartController();
                    controllerConnected = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } while (!controllerConnected);

        } while (true);
    }

    /**
     * Starts the motor controller.
     */
    private void doStartMotorController() {
        this.motorInterface = new MotorInterface(storageBox);
        this.motorInterface.start();

        if (slamRunning && !slam.getMotorActive()) {
            slam.doAddMotorInterface(this.motorInterface);
        }

        this.appRunning = true;
    }

    /**
     * Start slam-algorithm
     */
    private void doStartSlam() {

        this.slam = new Slam();
        this.lidar = new Lidar();

        if (this.lidar.doConnectLidar("/dev/ttyUSB0")) {
            this.lidar.setLidarValues(1, 1000);
            this.lidar.startLidarScan();

            this.slam.initSlam(lidar.getLidarDevice(), 1, storageBox);

            if (appRunning && !slam.getMotorActive()) {
                slam.doAddMotorInterface(this.motorInterface);
            }

            slam.start();

            this.slamRunning = true;
        }
    }

    /**
     * Starts the webcamera transmission to GUI.
     */
    private void doStartWebCam() {
        this.runWebcamera = new RunWebcamera();
        this.runWebcamera.start();
        this.camRunning = true;
    }

    /**
     * Stops the motor controller
     */
    private void doStopMotorController() {
        motorInterface.doStop();
        motorInterface.interrupt();

        if (slamRunning) {
            slam.shutDown();
        }

        this.appRunning = false;
    }

    /**
     * Stops the SLAM-algorithm
     */
    private void doStopSlam() {
        slam.shutDown();
        slam.close();
        slam.interrupt();
        lidar.close();
        this.slamRunning = false;
    }

    /**
     * Stops the webcam transmission to GUI.
     */
    private void doStopWebCam() {
        runWebcamera.stopCam();
        runWebcamera.interrupt();
        this.camRunning = false;
    }
}
