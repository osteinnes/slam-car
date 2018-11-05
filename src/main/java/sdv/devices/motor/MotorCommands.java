package sdv.devices.motor;

import sdv.networking.motor.TcpClient;

/**
 * MotorCommand class. Generalizes commands for motor controll
 * so that we can easily controll the input such as keyboard-arrows.
 * Thus, turnLeft, turnRight and so on..
 */
public class MotorCommands {

    // Client to the Python MotorControl server.
    private TcpClient Client;
    // Desired speed of motors.
    private int speed;

    /**
     * Constructor of the MotorCommands class. Sets speed to 0 (until user input) and sets client.
     * @param client Tcp-client of the Python server.
     */
    public MotorCommands(TcpClient client){
        speed = 0;
        this.Client = client;
    }

    /**
     * Turn the car left. Sends corresponding commands to the Python-server,
     */
    public void turnLeft(){
        Client.setBackwardMotor2Speed(speed);
        Client.setForwardMotor1Speed(speed);
        System.out.println("TURN LEFT REQUEST SENT.");
    }

    /**
     * Turn the car right. Sends corresponding commands to the Python-server
     */
    public void turnRight(){
        Client.setForwardMotor2Speed(speed);
        Client.setBackwardMotor1Speed(speed);
        System.out.println("TURN RIGHT REQUEST SENT.");
    }

    /**
     * Drive the car forward. Sends corresponding commands to the Python-server
     */
    public void forward(){
        Client.setForwardMotor1Speed(speed);
        Client.setForwardMotor2Speed(speed);
        System.out.println("FORWRAD REQUEST SENT");
    }

    /**
     * Drive the car backwards. Sends corresponding commands to the Python-server
     */
    public void reverse(){
        Client.setBackwardMotor1Speed(speed);
        Client.setBackwardMotor2Speed(speed);
        System.out.println("REVERSE REQUEST SENT");
    }

    /**
     * Stop the car. Sends corresponding commands to the Python-server
     */
    public void stop(){
        Client.setBackwardMotor1Speed(0);
        Client.setBackwardMotor2Speed(0);
        Client.setForwardMotor2Speed(0);
        Client.setForwardMotor1Speed(0);
        System.out.println("STOP REQUEST SENT");
    }

    /**
     * Returns speed of the car.
     * @return speed of the car.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set speed of car
     * @param speed of car
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
