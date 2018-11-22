package sdv.devices.motor;

import sdv.tools.boxes.EncoderBox;
import sdv.networking.motor.MotorClient;

import static java.lang.Thread.sleep;


public class EncoderThread implements Runnable {
    private MotorClient pythonClient;
    private MotorCommands motorCommands;
    private EncoderBox box;


    public EncoderThread(MotorClient motorClient, MotorCommands motorCommands, EncoderBox box) {
        this.pythonClient = motorClient;
        this.motorCommands = motorCommands;
        this.box = box;
    }

    @Override
    public void run() {
        box.start();
        while (true) {
                String[] s = getEncoder();
                box.setValue(s);

            try {
                sleep(999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     **/

    public String[] getEncoder() {
        String[] strings, s;
        motorCommands.getEncoderData();
        s = pythonClient.messageFromServer();
        if (!s[1].contains("FAILED") && !s[3].contains("FAILED")) {
            //System.out.println("Encoder1: " + pythonClient2.response[1]);
            //System.out.println("Encoder2: " + pythonClient2.response[3]);
            strings = s;
        } else {
            System.out.println("getEncoder() FAILED");
            strings = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
        }
        return strings;
    }
}
