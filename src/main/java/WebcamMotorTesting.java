import sdv.devices.camera.RunWebcamera;
import sdv.devices.motor.MotorController;

/**
 * @author Eirik G. Gustafsson
 * @version 05.11.2018.
 */
public class WebcamMotorTesting {
    public static void main(String[] args) {
        RunWebcamera WebCam = new RunWebcamera();
        MotorController motorController = new MotorController();

        WebCam.start();
        motorController.start();
    }
}
