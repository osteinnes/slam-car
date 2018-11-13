package sdv.devices.motor;

import sdv.networking.motor.TcpClient;


public class EncoderThread extends Thread{
    private TcpClient pythonClient = null;
    private MotorCommands motorCommands = null;

    public EncoderThread(TcpClient tcpClient, MotorCommands motorCommands){
        this.pythonClient = tcpClient;
        this.motorCommands = motorCommands;
    }

    @Override
    public void run(){
    }

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
