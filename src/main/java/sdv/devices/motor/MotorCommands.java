package sdv.devices.motor;

import sdv.networking.motor.TcpClient;

/**
 * MotorCommand class. Generalizes commands for motor controll
 * so that we can easily controll the input such as keyboard-arrows.
 * Thus, turnLeft, turnRight and so on..
 */
public class MotorCommands {

    // pythonClient to the Python MotorControl server.
    private TcpClient pythonClient;
    // Desired motorSpeed of motors.
    private int motorSpeed;

    private int turningSpeed;

    /**
     * Constructor of the MotorCommands class. Sets motorSpeed to 0 (until user input) and sets client.
     * @param client Tcp-client of the Python server.
     */
    public MotorCommands(TcpClient client){
        motorSpeed = 0;
        this.turningSpeed = 16;
        this.pythonClient = client;
    }

    /**
     * Turn the car right. Sends corresponding commands to the Python-server,
     */
    public void turnRight(){
        pythonClient.setBackwardMotor2Speed(turningSpeed);
        pythonClient.setForwardMotor1Speed(turningSpeed);
        //System.out.println("TURN LEFT REQUEST SENT.");
    }

    /**
     * Turn the car left. Sends corresponding commands to the Python-server
     */
    public void turnLeft(){
        pythonClient.setForwardMotor2Speed(turningSpeed);
        pythonClient.setBackwardMotor1Speed(turningSpeed);
        //System.out.println("TURN RIGHT REQUEST SENT.");
    }

    /**
     * Drive the car forward. Sends corresponding commands to the Python-server
     */
    public void forward(){
        pythonClient.setForwardMotor1Speed(motorSpeed);
        pythonClient.setForwardMotor2Speed(motorSpeed);
        //System.out.println("FORWRAD REQUEST SENT");
    }

    /**
     * Drive the car backwards. Sends corresponding commands to the Python-server
     */
    public void reverse(){
        pythonClient.setBackwardMotor1Speed(motorSpeed);
        pythonClient.setBackwardMotor2Speed(motorSpeed);
        //System.out.println("REVERSE REQUEST SENT");
    }

    /**
     * Stop the car. Sends corresponding commands to the Python-server
     */
    public void stop(){
        pythonClient.setBackwardMotor1Speed(0);
        pythonClient.setBackwardMotor2Speed(0);
        pythonClient.setForwardMotor2Speed(0);
        pythonClient.setForwardMotor1Speed(0);
        //System.out.println("STOP REQUEST SENT");
    }
    /**
     * Fetches encoder data for left and right encoder from Python server.
     */
    public void getEncoderData(){
        pythonClient.sendEncoderRequest();
        //System.out.println("ENCODERDATA REQUEST SENT");
    }

    /**
     * Returns motorSpeed of the car.
     * @return motorSpeed of the car.
     */
    public int getMotorSpeed() {
        return motorSpeed;
    }

    /**
     * Set motorSpeed of car
     * @param motorSpeed of car
     */
    public void setMotorSpeed(int motorSpeed) {
        this.motorSpeed = motorSpeed;
    }
}
