package sdv.app;

import sdv.networking.GuiServer;

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
    public boolean doStartController() {

        boolean status;

        if (!this.guiController.connected) {
            System.out.println("Waiting for connection from GUI-controller.");
            this.guiController.connect(8003);
            status = true;
        } else {
            status = false;
        }

        return status;
    }

    /**
     * Returns connection status of the GUI-controller
     *
     * @return connection status of the GUI-controller
     */
    public boolean isConnected() {
        return this.guiController.connected;
    }

    /**
     * Returns control message from the GUI-controller
     *
     * @return control message from the GUI-controller
     */
    public String getControlMsg() {
        this.guiController.messageFromClient();
        return this.guiController.getClientString();
    }

    /**
     * Closes the GUI-controller on disconnection.
     */
    public void closeController() {
        this.guiController.closeSocket();
    }
}
