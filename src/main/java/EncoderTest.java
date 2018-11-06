import sdv.devices.motor.MotorController;

public class EncoderTest {
    public static void main(String[] args) {
        MotorController motorController = new MotorController();


        motorController.start();

        while (true) {
            motorController.getEncoder();
        }
    }
}
