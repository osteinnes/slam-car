package sdv.devices.motor;

import sdv.networking.motor.TcpClient;

public class MotorCommands {

    private TcpClient Client;
    private int speed;

    public MotorCommands(TcpClient client){
        speed = 0;
        this.Client = client;
    }

    public void turnLeft(){
        Client.setBackwardMotor2Speed(speed);
        Client.setForwardMotor1Speed(speed);
        System.out.println("TURN LEFT REQUEST SENT.");
    }

    public void turnRight(){
        Client.setForwardMotor2Speed(speed);
        Client.setBackwardMotor1Speed(speed);
        System.out.println("TURN RIGHT REQUEST SENT.");
    }

    public void forward(){
        Client.setForwardMotor1Speed(speed);
        Client.setForwardMotor2Speed(speed);
        System.out.println("FORWRAD REQUEST SENT");
    }

    public void reverse(){
        Client.setBackwardMotor1Speed(speed);
        Client.setBackwardMotor2Speed(speed);
        System.out.println("REVERSE REQUEST SENT");
    }

    public void stop(){
        Client.setBackwardMotor1Speed(0);
        Client.setBackwardMotor2Speed(0);
        Client.setForwardMotor2Speed(0);
        Client.setForwardMotor1Speed(0);
        System.out.println("STOP REQUEST SENT");
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
