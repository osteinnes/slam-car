package sdv.devices.camera;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Captures image from web-cam and returns it.
 *
 * @author Eirik G. Gustafsson
 * @version 25.09.2018
 */
public class WebCam {
    // Pc's web-camera.
    private Webcam webcam;

    /**
     * Gets the laptop web-cam.
     */
    public WebCam() {
        doSetupWebcam();
    }

    /**
     * Readies the web-cam.
     */
    private void doSetupWebcam() {
        this.webcam = Webcam.getDefault();
        this.webcam.setViewSize(new Dimension(176,144));

        if (null != this.webcam) {
            System.out.println(this.webcam.getDevice().getName());
            this.webcam.open();

        } else {
            System.out.println("No camera detected.");
        }

    }

    /**
     * Captures BufferedImage and returns it as a Image.
     *
     * @return Returns image.
     */
    public BufferedImage doGetImage() {
        return webcam.getImage();
    }

    public void close() {
        webcam.close();
    }
}