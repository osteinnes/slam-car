package sdv.devices.camera;

import sdv.networking.camera.StreamVideo;

import java.io.IOException;

/**
 * @author Eirik G. Gustafsson
 * @version 05.11.2018.
 */
public class RunWebcamera extends Thread {

    @Override
    public void run() {

        StreamVideo streamVideo = new StreamVideo(8001);
        WebCam readWebcam = new WebCam();

        while (true) {

            if(!streamVideo.getIsConnected()) {
                streamVideo.doConnect();
            }

            try {
                sleep(3);
                streamVideo.doSendImage(readWebcam.doGetImage());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Thread.currentThread().isInterrupted()) {
                streamVideo.closeSocket();
            }
        }
    }
}
