import sdv.devices.motor.MotorController;

/**
 * Test-class reading encoders of the motor-controller, while the car can be driven from GUI.
 *
 * @author Ole-martin Steinnes
 */
public class EncoderTest {
    public static void main(String[] args) {
        MotorController motorController = new MotorController();


        motorController.start();

        while (true) {
            motorController.getEncoder();
        }
    }
}
