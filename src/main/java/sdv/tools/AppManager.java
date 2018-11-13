package sdv.tools;



/**
 * @author Ole-martin Steinnes
 */
public class AppManager {

    private AppController appController;

    public AppManager() {
        doSetUpApp();
    }

    private void doSetUpApp() {
        this.appController = new AppController();
    }


    private void doProgramLogic() {

    }
}
