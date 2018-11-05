

import sdv.devices.motor.MotorCommands;
import sdv.networking.motor.TcpClient;
import sdv.networking.motor.TcpServer;
import java.util.Scanner;

class MotorControllerTest {

    public static String[] inputParser(String keyword) {
        return keyword.split(":");
    }

    public static void main(String argv[]) throws Exception {
        boolean run = true;

        TcpClient Client = new TcpClient();
        MotorCommands Commands = new MotorCommands(Client);
        TcpServer Server = new TcpServer();

        String keyword1 = "";
        String keyword2 = "";

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Server Connection Status: " + Client.connected);
        System.out.println("Are any clients Connected: " + Server.connected);

        if (!Client.connected) {
            Client.connect();
            System.out.println("Second " + Client.connected);
        }

        if (!Server.connected) {
            System.out.println("Waiting for connection");
            Server.connect();
        }

        while (run) {
            System.out.println("Entering while");
            Server.messageFromClient();
            String text = Server.clientString;
            // String text = keyboard.nextLine();
            String[] input = inputParser(text);

            if (input.length == 2) {
                keyword1 = input[0];
                keyword2 = input[1];
            }

            System.out.println("key1: " + keyword1 + " key2: " + keyword2);

            if (keyword1.toLowerCase().equals("speed")) {
                int speed = Integer.parseInt(keyword2);
                Commands.setSpeed(speed);
                System.out.println("Speed set:" + speed);
            }

            if (text.toLowerCase().trim().equals("getencoder")) {
                for (int i = 0; i < 10; i++) {
                    Client.sendEncoderRequest();
                    System.out.println("Request sent. Waiting for response");
                }
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("left")) {
                Commands.turnLeft();
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("right")) {
                Commands.turnRight();
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("up")) {
                Commands.forward();
            }
            if (keyword1.toLowerCase().equals("key_pressed") && keyword2.toLowerCase().equals("down")) {
                Commands.reverse();
            }
            if (text.toLowerCase().trim().equals("getspeed")) {
                Client.sendMotorSpeedRequest();
            }
            if (keyword1.toLowerCase().equals("key_released")){
                Commands.stop();
            }
            if (text.toLowerCase().trim().equals("exit")) {
                System.out.println("Shutting down");
                run = false;
            }

        }
    }
}