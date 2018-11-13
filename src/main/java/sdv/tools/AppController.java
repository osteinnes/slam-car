package sdv.tools;

import sdv.networking.motor.GuiServer;

/**
 * @author Ole-martin Steinnes
 */
public class AppController {

    private GuiServer guiController;

    public AppController() {

        doSetUpFields();
        doStartController();

    }

    private void doSetUpFields() {
        this.guiController = new GuiServer();
    }

    private void doStartController() {
        if (!this.guiController.connected) {
            this.guiController.connect(8003);
        }
    }
}
