package sdv.devices.motor;

import sdv.tools.threading.boxes.EncoderBox;
import sdv.networking.GuiServer;
import sdv.networking.TcpClient;

public class MotorInterface extends Thread {
    // Object containing the pythonClient
    private TcpClient pythonClient;

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

    public MotorInterface(EncoderBox box) {
        setUpFields();
        setUpConnection();
        setUpThreads();
        this.box = box;
    }

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
        this.pythonClient = new TcpClient();
        this.motorCommands = new MotorCommands(pythonClient);
        this.guiServer = new GuiServer();
    }

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

    public String[] fetchEncoderData(){
        return this.encoderThread.getEncoder();
    }

    public void doStop(){
        motorCommands.stop();
        guiServer.closeSocket();
        pythonClient.closeSocket();
        cThread.interrupt();
        eThread.interrupt();
    }
}
