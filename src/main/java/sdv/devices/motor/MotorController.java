package sdv.devices.motor;

import sdv.networking.motor.TcpClient;
import sdv.networking.motor.TcpServer;

import java.util.Scanner;

/**
 * Responsible for motor control. Uses MotorCommands to relay input from GUI-client to Python-server.
 *
 * @author Ole-martin Steinnes
 */
public class MotorController extends Thread {

    // Boolean controlling whether comm-protocol with GUI should run.
    private boolean run = true;

    // Object containing the pythonClient
    private TcpClient pythonClient;
    private TcpClient pythonClient2;

    // Object containing the motor motorCommands protocol
    private MotorCommands motorCommands;
    private MotorCommands motorCommands2;

    // Object containing the Python-client
    private TcpServer guiServer;

    // Keywords from guiClient.
    private String guiKeyword1;
    private String guiKeyword2;

    private Scanner keyboard;

    /**
     * Constructor of the MotorController
     */
    public MotorController(){
        setUpFields();
        setUpConnection();
    }

    /**
     * Creates a message protocol between GUI-client and Python-server in a new thread.
     * Enabling the GUI to control motors.
     */
    public void run() {
        while (run) {
            System.out.println("Entering while");
            guiServer.messageFromClient();
            String text = guiServer.clientString;
            // String text = keyboard.nextLine();
            String[] guiClientInput = inputParser(text);

            if (guiClientInput.length == 2) {
                guiKeyword1 = guiClientInput[0];
                guiKeyword2 = guiClientInput[1];
            }

            System.out.println("key1: " + guiKeyword1 + " key2: " + guiKeyword2);

            if (guiKeyword1.toLowerCase().equals("speed")) {
                int speed = Integer.parseInt(guiKeyword2);
                motorCommands.setMotorSpeed(speed);
                System.out.println("Speed set:" + speed);
            }

            if (text.toLowerCase().trim().equals("getencoder")) {
                motorCommands.getEncoderData();
                pythonClient.messageFromServer();
                System.out.println("Encoder1: " + pythonClient.response[1]);
                System.out.println("Encoder2: " + pythonClient.response[3]);

            }
            if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("left")) {
                motorCommands.turnLeft();
            }
            if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("right")) {
                motorCommands.turnRight();
            }
            if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("up")) {
                motorCommands.forward();
            }
            if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("down")) {
                motorCommands.reverse();
            }
            if (text.toLowerCase().trim().equals("getspeed")) {
                pythonClient.sendMotorSpeedRequest();
            }
            if (guiKeyword1.toLowerCase().equals("key_released")) {
                motorCommands.stop();
            }
            if (text.toLowerCase().trim().equals("exit")) {
                System.out.println("Shutting down");
                run = false;
            }
        }
    }


    /**
     * Set up fields for motor controller
     */
    private void setUpFields() {
        this.pythonClient = new TcpClient();
        this.pythonClient2 = new TcpClient();
        this.motorCommands = new MotorCommands(pythonClient);
        this.motorCommands2 = new MotorCommands(pythonClient2);
        this.guiServer = new TcpServer();

        this.guiKeyword1 = "";
        this.guiKeyword2 = "";

        this.keyboard = new Scanner(System.in);
    }

    /**
     * Set up connection for motor control
     */
    private void setUpConnection(){
        if (!pythonClient.connected) {
            pythonClient.connect();
            System.out.println("Second " + pythonClient.connected);
        }

        if (!pythonClient2.connected) {
            pythonClient2.connect();
            System.out.println("Third " + pythonClient2.connected);
        }

        if (!guiServer.connected) {
            System.out.println("Waiting for connection");
            guiServer.connect();
        }
    }

    /**
     * Parses input keyword from pythonClient (GUI)
     *
     * @param keyword from pythonClient (GUI)
     * @return String-array containing the input from pythonClient split by colon (:)
     */
    public String[] inputParser(String keyword) {
        return keyword.split(":");
    }

    /**
     * Gets encoder data from the motor controller, using the MotorCommands and TcpClient classes.
     * Recieves encoder data from Python-guiServer and makes sure that the data recieved is in the correct format.
     * Furthermore, it returns a string array of encoder values (enc1: int, enc2: int)
     *
     * @return a string array of encoder values (enc1: int, enc2: int)
     */
    public String[] getEncoder() {
        String[] strings;
        motorCommands2.getEncoderData();
        pythonClient2.messageFromServer();
        if (!pythonClient2.response[1].contains("FAILED") || !pythonClient2.response[3].contains("FAILED")) {
            System.out.println("Encoder1: " + pythonClient2.response[1]);
            System.out.println("Encoder2: " + pythonClient2.response[3]);
            strings = pythonClient2.response;
        } else {
            strings = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
        }
        return strings;
    }
}
