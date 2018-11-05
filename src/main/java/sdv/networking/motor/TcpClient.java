package sdv.networking.motor;

import java.io.*;
import java.net.Socket;

public class TcpClient {

    String serverResponse;
    public Boolean connected;
    BufferedReader in;
    PrintWriter pw;


    public TcpClient(){
        connected = false;
    }

    public boolean connect(){
        try {
            if(!connected) {
                Socket clientSocket = new Socket("localhost", 2004);
                System.out.println("Connected to serever at " + clientSocket.getRemoteSocketAddress());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pw = new PrintWriter(clientSocket.getOutputStream(), true);
                connected = true;
            }
        }
        catch (IOException ex){
            System.out.println(ex.toString());
        }
        return connected;
    }
//    public void messageFromServer(){
//        try {
//                     serverResponse = in.readLine();
//                      if(serverResponse != null) {
//                          serverResponse = serverResponse.replaceAll("\\p{P}","");
//                          System.out.println("From server " + serverResponse);
//                      }
//                      else{
//                          System.out.println("No response");
//                    }
//              }
//        catch (IOException ex){
//            System.out.println(ex.toString());
//        }
//    }

    public void sendEncoderRequest() {
        System.out.println("Sending Encoder Request to Server");
        pw.println("getEncoderData");
    }
    public void sendMotorSpeedRequest(){
        System.out.println("Sending Motorspeed Request");
        pw.println("getMotorSpeed");
    }
    public void setForwardMotor1Speed(int motor1Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setforwardspeedmotorone." + "motorone:" + motor1Speed;
        System.out.println(payload);
        pw.println(payload);
    }
    public void setForwardMotor2Speed(int motor2Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setforwardspeedmotortwo." + "motortwo:" + motor2Speed;
        System.out.println(payload);
        pw.println(payload);
    }

    public void setBackwardMotor1Speed(int motor1Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setbackwardspeedmotorone." + "motorone:" + motor1Speed;
        System.out.println(payload);
        pw.println(payload);
    }

    public void setBackwardMotor2Speed(int motor2Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setbackwardspeedmotortwo." + "motortwo:" + motor2Speed;
        System.out.println(payload);
        pw.println(payload);
    }

}

