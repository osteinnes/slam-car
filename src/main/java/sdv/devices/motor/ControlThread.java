package sdv.devices.motor;

import sdv.networking.motor.GuiServer;
import sdv.networking.motor.TcpClient;

class ControlThread implements Runnable {

    private MotorCommands motorCommands = null;
    private GuiServer guiServer = null;
    private TcpClient pythonClient = null;

    // Keywords from guiClient.
    private String guiKeyword1;
    private String guiKeyword2;
    private boolean run = true;

    public ControlThread(TcpClient client, MotorCommands motorCommands, GuiServer guiServer) {
        this.motorCommands = motorCommands;
        this.guiServer = guiServer;
        this.pythonClient = client;
        this.guiKeyword1 = "";
        this.guiKeyword2 = "";
    }

    public void run() {
        boolean ready = true;
        while (run) {
            //System.out.println("Entering while");
            guiServer.messageFromClient();
            String text = guiServer.clientString;
            System.out.println("ControlThread MSC: " + text);
            // String text = keyboard.nextLine();
            String[] guiClientInput = inputParser(text);

            if (guiClientInput.length == 2) {
                guiKeyword1 = guiClientInput[0];
                guiKeyword2 = guiClientInput[1];
            }

            if (guiKeyword1.toLowerCase().equals("speed")) {
                ready = false;
                int speed = Integer.parseInt(guiKeyword2);
                motorCommands.setMotorSpeed(speed);
                //System.out.println("Speed set:" + speed);
            }

            if (ready) {
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("left")) {
                    ready = false;
                    motorCommands.turnLeft();
                }
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("right")) {
                    ready = false;
                    motorCommands.turnRight();
                }
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("up")) {
                    ready = false;
                    motorCommands.forward();
                }
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("down")) {
                    ready = false;
                    motorCommands.reverse();
                }
                if (text.toLowerCase().trim().equals("getspeed")) {
                    ready = false;
                    pythonClient.sendMotorSpeedRequest();
                }
            }
            if (!ready) {
                if (guiKeyword1.toLowerCase().equals("key_released")) {
                    motorCommands.stop();
                    ready = true;
                }

            }
            if (text.toLowerCase().trim().equals("exit")) {
                System.out.println("Shutting down");
                run = false;
            }
        }
    }
    private String[] inputParser (String keyword){
        return keyword.split(":");
    }
}