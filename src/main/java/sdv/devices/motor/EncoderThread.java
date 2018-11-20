package sdv.devices.motor;

import sdv.algorithms.tools.StorageBox;
import sdv.networking.motor.TcpClient;


public class EncoderThread implements Runnable{
    private TcpClient pythonClient;
    private MotorCommands motorCommands;
    private StorageBox box;


    public EncoderThread(TcpClient tcpClient, MotorCommands motorCommands, StorageBox box){
        this.pythonClient = tcpClient;
        this.motorCommands = motorCommands;
        this.box = box;
    }

    @Override
    public void run(){
        while(true)
            if(box.getRun()) {
                box.setValue(getEncoder());
                box.setRun(false);
                box.setRead(true);
            }
    }

    /**
     *
     * @return
     **/

    public String[] getEncoder() {
        String[] strings;
        motorCommands.getEncoderData();
        pythonClient.messageFromServer();
        if(!pythonClient.response[1].contains("FAILED") && !pythonClient.response[3].contains("FAILED")) {
            //System.out.println("Encoder1: " + pythonClient2.response[1]);
            //System.out.println("Encoder2: " + pythonClient2.response[3]);
            strings = pythonClient.response;
        } else {
            strings = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
        }
        return strings;
    }
}
