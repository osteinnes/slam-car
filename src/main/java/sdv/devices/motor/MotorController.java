package sdv.devices.motor;

import sdv.networking.motor.TcpClient;
import sdv.networking.motor.TcpServer;

import java.util.Scanner;

/**
 * Controlles motor of the car.
 *
 * @author Ole-martin Steinnes
 */
public class MotorController{


    private boolean run = true;

    private TcpClient client;
    private MotorCommands commands;
    private TcpServer server;

    private String keyword1;
    private String keyword2;

    private Scanner keyboard;

    /**
     * Constructor of the MotorController
     */
    public MotorController() {
        setUpFields();
        setUpConnection();
    }


    public void run() {
        while (run) {
            System.out.println("Entering while");
            server.messageFromClient();
            String text = server.clientString;
            // String text = keyboard.nextLine();
            String[] input = inputParser(text);

            if (input.length == 2) {
                keyword1 = input[0];
                keyword2 = input[1];
            }

            System.out.println("key1: " + keyword1 + " key2: " + keyword2);

            if (keyword1.toLowerCase().equals("speed")) {
                int speed = Integer.parseInt(keyword2);
                commands.setSpeed(speed);
                System.out.println("Speed set:" + speed);
            }

            if (text.toLowerCase().trim().equals("getencoder")) {
                for (int i = 0; i < 10; i++) {
                    client.sendEncoderRequest();
                    System.out.println("Request sent. Waiting for response");
                }
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("left")) {
                commands.turnLeft();
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("right")) {
                commands.turnRight();
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("up")) {
                commands.forward();
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("down")) {
                commands.reverse();
            }
            if (text.toLowerCase().trim().equals("getspeed")) {
                client.sendMotorSpeedRequest();
            }
            if (keyword1.toLowerCase().equals("key_released")) {
                commands.stop();
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
        this.client = new TcpClient();
        this.commands = new MotorCommands(client);
        this.server = new TcpServer();

        this.keyword1  = "";
        this.keyword2 = "";

        this.keyboard = new Scanner(System.in);
    }

    /**
     * Set up connection for motor control
     */
    private void setUpConnection(){
        if (!client.connected) {
            client.connect();
            System.out.println("Second " + client.connected);
        }

        if (!server.connected) {
            System.out.println("Waiting for connection");
            server.connect();
        }
    }

    public String[] inputParser(String keyword) {
        return keyword.split(":");
    }
}
