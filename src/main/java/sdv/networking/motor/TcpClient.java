package sdv.networking.motor;

import java.io.*;
import java.net.Socket;

/**
 * TcpClient-class that connects to a Python-server that
 * directly communicates with the Motor controller over USB.
 */
public class TcpClient {

    // Response from server
    String serverResponse;
    // Boolean for connected status.
    public Boolean connected;
    // Input from server.
    BufferedReader in;
    // Output to server.
    PrintWriter pw;


    /**
     * Constructor of the TcpClient-class. Sets connected false initially.
     */
    public TcpClient(){
        connected = false;
    }

    /**
     * Connects to the local Python-server.
     *
     * Returns connected status
     * @return connected status
     */
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

    /**
     * Sends request to get encoder data to the server
     */
    public void sendEncoderRequest() {
        System.out.println("Sending Encoder Request to Server");
        pw.println("getEncoderData");
    }

    /**
     * Sends request to get motor speed from the server.
     */
    public void sendMotorSpeedRequest(){
        System.out.println("Sending Motorspeed Request");
        pw.println("getMotorSpeed");
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 1 in forward direction.
     *
     * @param motor1Speed desired speed for motor 1
     */
    public void setForwardMotor1Speed(int motor1Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setforwardspeedmotorone." + "motorone:" + motor1Speed;
        System.out.println(payload);
        pw.println(payload);
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 2 in forward direction.
     *
     * @param motor2Speed desired speed for motor 2
     */
    public void setForwardMotor2Speed(int motor2Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setforwardspeedmotortwo." + "motortwo:" + motor2Speed;
        System.out.println(payload);
        pw.println(payload);
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 1 in backward direction.
     *
     * @param motor1Speed desired speed for Motor 1
     */
    public void setBackwardMotor1Speed(int motor1Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setbackwardspeedmotorone." + "motorone:" + motor1Speed;
        System.out.println(payload);
        pw.println(payload);
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 2 in backward direction.
     *
     * @param motor2Speed desired speed for motor 2
     */
    public void setBackwardMotor2Speed(int motor2Speed){
        System.out.println("Sending Set Motorspeed Request");
        String payload = "setbackwardspeedmotortwo." + "motortwo:" + motor2Speed;
        System.out.println(payload);
        pw.println(payload);
    }

}

