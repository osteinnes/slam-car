package sdv.devices.motor;

import sdv.tools.threading.boxes.EncoderBox;
import sdv.networking.TcpClient;

import static java.lang.Thread.sleep;


public class EncoderThread implements Runnable {
    private TcpClient pythonClient;
    private MotorCommands motorCommands;
    private EncoderBox box;


    public EncoderThread(TcpClient tcpClient, MotorCommands motorCommands, EncoderBox box) {
        this.pythonClient = tcpClient;
        this.motorCommands = motorCommands;
        this.box = box;
    }

    @Override
    public void run() {
        while (true) {
            box.setValue(getEncoder());
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     **/

    public String[] getEncoder() {
        String[] strings;
        motorCommands.getEncoderData();
        pythonClient.messageFromServer();
        if (!pythonClient.response[1].contains("FAILED") && !pythonClient.response[3].contains("FAILED")) {
            //System.out.println("Encoder1: " + pythonClient2.response[1]);
            //System.out.println("Encoder2: " + pythonClient2.response[3]);
            strings = pythonClient.response;
        } else {
            strings = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
        }
        return strings;
    }
}
