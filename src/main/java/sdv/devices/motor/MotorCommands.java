package sdv.devices.motor;

import sdv.networking.motor.MotorClient;

/**
 * MotorCommand class. Generalizes commands for motor controll
 * so that we can easily controll the input such as keyboard-arrows.
 * Thus, turnLeft, turnRight and so on..
 */
public class MotorCommands {

    // pythonClient to the Python MotorControl server.
    private MotorClient pythonClient;
    // Desired motorSpeed of motors.
    private int motorSpeed;

    /**
     * Constructor of the MotorCommands class. Sets motorSpeed to 0 (until user input) and sets client.
     * @param client Tcp-client of the Python server.
     */
    public MotorCommands(MotorClient client){
        motorSpeed = 32;
        this.pythonClient = client;
    }

    /**
     * Turn the car right. Sends corresponding commands to the Python-server,
     */
    public void turnRight(){
        pythonClient.setBackwardMotor2Speed(motorSpeed);
        pythonClient.setForwardMotor1Speed(motorSpeed);
    }

    /**
     * Turn the car left. Sends corresponding commands to the Python-server
     */
    public void turnLeft(){
        pythonClient.setForwardMotor2Speed(motorSpeed);
        pythonClient.setBackwardMotor1Speed(motorSpeed);
    }

    /**
     * Drive the car forward. Sends corresponding commands to the Python-server
     */
    public void forward(){
        pythonClient.setForwardMotor1Speed(motorSpeed);
        pythonClient.setForwardMotor2Speed(motorSpeed);
    }

    /**
     * Drive the car backwards. Sends corresponding commands to the Python-server
     */
    public void reverse(){
        pythonClient.setBackwardMotor1Speed(motorSpeed);
        pythonClient.setBackwardMotor2Speed(motorSpeed);
    }

    /**
     * Stop the car. Sends corresponding commands to the Python-server
     */
    public void stop(){
        pythonClient.setStop();
    }

    /**
     * Fetches encoder data for left and right encoder from Python server.
     */
    public void getEncoderData(){
        pythonClient.sendEncoderRequest();
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
