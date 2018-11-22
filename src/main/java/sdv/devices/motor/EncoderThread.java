package sdv.devices.motor;

import sdv.tools.boxes.EncoderBox;
import sdv.networking.motor.MotorClient;

/**
 * Runnable thread that sends encoder requests
 * to the python server and stores the values
 * in a shared storage box.
 */

public class EncoderThread implements Runnable {

    private MotorClient pythonClient;
    private MotorCommands motorCommands;
    private EncoderBox box;

    /**
     * Constructor
     * @param motorClient Inherits an motorClient object which contains a tcp connection.
     * @param motorCommands Set of predefined functions used to send encoder request.
     * @param box Storage box for storing encoder values
     */
    public EncoderThread(MotorClient motorClient, MotorCommands motorCommands, EncoderBox box) {
        this.pythonClient = motorClient;
        this.motorCommands = motorCommands;
        this.box = box;
    }

    public void run() {
        box.start();
        while (true) {
            //Fetches encoder data with 1 second interval
            long time = System.currentTimeMillis();
            String[] s = getEncoder();
            box.setValue(s);
            //Prints delta time between fetching and setting values in storagebox
            System.out.println("Time since getEncoder(): " + (System.currentTimeMillis() - time));
            try {
                Thread.sleep(999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("After sleep: " + (System.currentTimeMillis() - time));
        }
    }

    /**Uses the getEncoderData function found in motor commands
     * to send an request to the python server for encoder data.
     * Returns previous values for encoder if reading failed.
     * Returns no response if the server is unresponsive
     * @return Encoder values in a string array.
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
