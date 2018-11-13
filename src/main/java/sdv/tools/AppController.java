package sdv.tools;

import sdv.networking.motor.GuiServer;

/**
 * The AppController-class creates an interface with our GUI. The GUI
 * will be able to control which program features are to be utilized
 * in the main program.
 *
 * @author Ole-martin Steinnes
 */
public class AppController {

    private GuiServer guiController;

    /**
     * Constructor of the AppController-class.
     */
    public AppController() {

        doSetUpFields();
        doStartController();

    }

    /**
     * Sets up fields of the AppController-class
     */
    private void doSetUpFields() {
        this.guiController = new GuiServer();
    }

    /**
     * Activates the socket-connection with the GUI.
     */
    public void doStartController() {
        if (!this.guiController.connected) {
            System.out.println("Waiting for connection from GUI-controller.");
            this.guiController.connect(8003);
        }
    }

    public boolean isConnected() {
        return this.guiController.connected;
    }

    public String getControlMsg() {
        this.guiController.messageFromClient();
        return this.guiController.getClientString();
    }
}
