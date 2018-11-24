package sdv.algorithms.slam;

import edu.wlu.cs.levy.breezyslam.robots.WheeledRobot;

/**
 * Robot-class that represents a WheeledRobot from the Breezyslam-library
 * Specifies wheel radius and half axle length in mm, calculating odometry.
 * This allows the library to calculate the change of position of the robot
 * based on encoder data from motor controller.
 *
 * @author Ole-martin Steinnes
 */
public class Robot extends WheeledRobot{

    /**
     * Constructor of the Robot-class. Take in metrics that are needed for Robot-odometry.
     *
     * @param wheel_radius_mm       Radius of wheel in millimeters.
     * @param half_axle_length_mm   Half axle length of in millimeters.
     */
    public Robot(double wheel_radius_mm, double half_axle_length_mm){
        super(wheel_radius_mm,half_axle_length_mm);
    }

    @Override
    protected WheelOdometry extractOdometry(double timestamp, double left_wheel_odometry, double right_wheel_odometry) {
        // Return odometry to library.
        return new WheelOdometry(timestamp,left_wheel_odometry,right_wheel_odometry);
    }

    @Override
    protected String descriptorString() {
        return null;
    }
}