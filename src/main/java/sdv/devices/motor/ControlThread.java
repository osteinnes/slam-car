package sdv.devices.motor;

import sdv.networking.gui.GuiServer;
import sdv.networking.motor.MotorClient;

class ControlThread implements Runnable {

    private MotorCommands motorCommands = null;
    private GuiServer guiServer = null;
    private MotorClient pythonClient = null;

    // Keywords from guiClient.
    private String guiKeyword1;
    private String guiKeyword2;
    private boolean run = true;

    public ControlThread(MotorClient client, MotorCommands motorCommands, GuiServer guiServer) {
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
            String text = guiServer.getClientString();
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
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("a")) {
                    ready = false;
                    motorCommands.turnLeft();
                }
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("d")) {
                    ready = false;
                    motorCommands.turnRight();
                }
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("w")) {
                    ready = false;
                    motorCommands.forward();
                }
                if (guiKeyword1.toLowerCase().equals("key_pressed") && guiKeyword2.toLowerCase().equals("s")) {
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