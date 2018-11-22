package sdv.devices.motor;

import sdv.tools.boxes.EncoderBox;
import sdv.networking.gui.GuiServer;
import sdv.networking.motor.MotorClient;

/**
 * A collection of threads and functions relating to motor control.
 * Initializes and starts both encoder thread and motor control thread.
 * Connects to the gui server.
 */
public class MotorInterface extends Thread {
    // Object containing the pythonClient
    private MotorClient pythonClient;

    // Object containing the motor motorCommands protocol
    private MotorCommands motorCommands;


    // Object containing the Python-client
    private GuiServer guiServer;

    //Threads containing controls for motor and encoder
    private ControlThread controlThread;
    private EncoderThread encoderThread;
    private Thread cThread;
    private Thread eThread;

    //Storage box
    private EncoderBox box;

    /**
     * Constructor
     * @param box StorageBox to store Encoder Values
     */

    public MotorInterface(EncoderBox box) {
        this.box = box;
        setUpFields();
        setUpConnection();
        setUpThreads();
    }

    /**
     * Starts encoder thread and control thread.
     */
    public void run() {
        try {
            eThread.start();
            cThread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Set up fields for motor controller
     */

    private void setUpFields() {
        this.pythonClient = new MotorClient();
        this.motorCommands = new MotorCommands(pythonClient);
        this.guiServer = new GuiServer();
    }

    /**
     * Sets up connection the python client
     * and the GUI server
     */
    private void setUpConnection() {
        if (!pythonClient.connected) {
            pythonClient.connect();
            //System.out.println("Second " + pythonClient.connected);
        }
        if (!guiServer.connected) {
            System.out.println("Waiting for connection");
            guiServer.connect(8000);
        }
    }

    /**
     * Sets up threads.
     */
    private void setUpThreads() {
        controlThread = new ControlThread(pythonClient, motorCommands, guiServer);
        cThread = new Thread(controlThread);
        cThread.setDaemon(true);
        cThread.setPriority(2);
        encoderThread = new EncoderThread(pythonClient,motorCommands, box);
        eThread = new Thread(encoderThread);
        eThread.setDaemon(true);
        eThread.setPriority(6);

    }

    /**
     * Stops everything.
     */
    public void doStop(){
        motorCommands.stop();
        guiServer.closeSocket();
        pythonClient.closeSocket();
        cThread.interrupt();
        eThread.interrupt();
    }
}
