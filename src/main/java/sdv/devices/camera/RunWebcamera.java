package sdv.devices.camera;

import sdv.networking.camera.StreamVideo;

/**
 * @author Eirik G. Gustafsson
 * @version 05.11.2018.
 */
public class RunWebcamera extends Thread {

    @Override
    public synchronized void start() {

        StreamVideo streamVideo = new StreamVideo(8001);
        WebCam readWebcam = new WebCam();

        while (true) {
            try {
                streamVideo.doSendImage(readWebcam.doGetImage());
            } catch (Exception exc) {

            }
        }

    }

    public static void main(String[] args) {
        RunWebcamera camera = new RunWebcamera();
        camera.start();
    }
}
