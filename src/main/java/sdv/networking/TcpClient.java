package sdv.networking;

import java.io.*;
import java.net.Socket;

/**
 * TcpClient-class that connects to a Python-server that
 * directly communicates with the Motor controller over USB.
 */
public class TcpClient {

    // Response from server
    private String serverResponse;
    // Boolean for connected status.
    public Boolean connected;
    // Input from server.
    private BufferedReader in;
    // Output to server.
    private PrintWriter pw;
    public String[] response;

    private Socket clientSocket;

    /**
     * Constructor of the TcpClient-class. Sets connected false initially.
     */
    public TcpClient() {
        connected = false;
    }

    /**
     * replaceAll
     * Connects to the local Python-server.
     * <p>
     * Returns connected status
     *
     * @return connected status
     */
    public boolean connect() {
        try {
            if (!connected) {
                clientSocket = new Socket("localhost", 2004);
                System.out.println("Connected to server at " + clientSocket.getRemoteSocketAddress());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pw = new PrintWriter(clientSocket.getOutputStream(), true);
                connected = true;
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return connected;
    }


    public void messageFromServer() {
        try {
            if (in.ready()) {
                serverResponse = in.readLine();
                // serverResponse = serverResponse.replaceAll("\\p{P}","");
                response = serverResponse.split(":");
                //System.out.println("From server " + serverResponse);

            } else {
                System.out.println("No response");
                response = new String[]{"NO RESPONSE", "NO RESPONSE", "NO RESPONSE", "NO RESPONSE"};
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * Sends request to get encoder data to the server
     */
    public void sendEncoderRequest() {
        //System.out.println("Sending Encoder Request to Server");
        pw.println("getEncoderData");
    }

    /**
     * Sends request to get motor speed from the server.
     */
    public void sendMotorSpeedRequest() {
        //System.out.println("Sending Motorspeed Request");
        pw.println("getMotorSpeed");
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 1 in forward direction.
     *
     * @param motor1Speed desired speed for motor 1
     */
    public void setForwardMotor1Speed(int motor1Speed) {
        //System.out.println("Sending Set Motorspeed Request");
        String payload = "setforwardspeedmotorone." + "motorone:" + motor1Speed;
        //System.out.println(payload);
        pw.println(payload);
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 2 in forward direction.
     *
     * @param motor2Speed desired speed for motor 2
     */
    public void setForwardMotor2Speed(int motor2Speed) {
        //System.out.println("Sending Set Motorspeed Request");
        String payload = "setforwardspeedmotortwo." + "motortwo:" + motor2Speed;
        //System.out.println(payload);
        pw.println(payload);
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 1 in backward direction.
     *
     * @param motor1Speed desired speed for Motor 1
     */
    public void setBackwardMotor1Speed(int motor1Speed) {
        //System.out.println("Sending Set Motorspeed Request");
        String payload = "setbackwardspeedmotorone." + "motorone:" + motor1Speed;
        //System.out.println(payload);
        pw.println(payload);
    }

    /**
     * Sends a command to the server containing the desired
     * motor speed for Motor 2 in backward direction.
     *
     * @param motor2Speed desired speed for motor 2
     */
    public void setBackwardMotor2Speed(int motor2Speed) {
        //System.out.println("Sending Set Motorspeed Request");
        String payload = "setbackwardspeedmotortwo." + "motortwo:" + motor2Speed;
        //System.out.println(payload);
        pw.println(payload);
    }

    /**
     *
     */
    public void setStop() {
        String payload = "stop";
        pw.println(payload);
    }

    public void closeSocket() {
        try {
            in.close();
            pw.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

