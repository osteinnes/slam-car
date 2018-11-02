package sdv.sensors.imu;

/**
 * Class representing IMU-sensor with displacement values, such as; mm, degrees and time.
 *
 * @author Ole-martin Steinnes
 * @version 0.1
 */
public class Imu {

    private double dxyMm;
    private double dthetaDegrees;
    private double dtSeconds;

    /**
     * Constructor of the IMU-class.
     */
    public Imu() {
    }

    /**
     * @param dxyMm displacement in mm
     */
    public void setDxyMm(double dxyMm) {
        this.dxyMm = dxyMm;
    }

    /**
     * @param dthetaDegrees displacement in degrees
     */
    public void setDthetaDegrees(double dthetaDegrees) {
        this.dthetaDegrees = dthetaDegrees;
    }

    /**
     * @param dtSeconds displacement in time(Seconds)
     */
    public void setDtSeconds(double dtSeconds) {
        this.dtSeconds = dtSeconds;
    }

    /**
     * @return displacement in mm
     */
    public double getDxyMm() {
        return dxyMm;
    }

    /**
     * @return displacement in degrees
     */
    public double getDthetaDegrees() {
        return dthetaDegrees;
    }

    /**
     * @return displacement in degrees
     */
    public double getDtSeconds() {
        return dtSeconds;
    }
}
