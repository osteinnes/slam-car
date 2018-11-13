package sdv.devices.motor;

import sdv.networking.motor.GuiServer;
import sdv.networking.motor.TcpClient;

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
    private Thread thread;

    public MotorInterface() {
        setUpFields();
        setUpConnection();
        setUpThreads();
    }

    public void run() {
        try {
            thread.start();
            System.out.println("State of Control Thread: " + thread.getState());
            this.encoderThread.start();
            System.out.println("State of Encoder Thread: " + this.encoderThread.getState());

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
        thread = new Thread(controlThread);
        thread.setDaemon(true);
        thread.setPriority(2);
        encoderThread = new EncoderThread(pythonClient,motorCommands);
        this.encoderThread.setDaemon(true);
        this.encoderThread.setPriority(6);
    }

    public String[] fetchEncoderData(){
        return this.encoderThread.getEncoder();
    }

    public void doStop(){
        motorCommands.stop();
        guiServer.closeSocket();
        pythonClient.closeSocket();
        thread.interrupt();
        this.encoderThread.interrupt();
    }
}
