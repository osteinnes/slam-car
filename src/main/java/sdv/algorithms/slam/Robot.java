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



        // Calculating the degrees of the wheels based on encoder data.
        //double left_wheel_encoder = left_wheel_odometry * (360.0/8400.0);
        //double right_wheel_encoder = right_wheel_odometry * (360.0/8400.0);

        //double left_wheel_radians = (java.lang.Math.toRadians(Math.abs(left_wheel_odometry)*((2*Math.PI)/8400.0))) % (2*Math.PI);
        //double right_wheel_radians = (java.lang.Math.toRadians(Math.abs(right_wheel_odometry))*((2*Math.PI)/8400.0)) % (2*Math.PI);

        //System.out.println("Left_wheel_radians: " + left_wheel_radians + " -- Right_wheel_radians: " + right_wheel_radians);


        //double left_wheel_encoder = (360/(2*Math.PI))*left_wheel_radians;
        //double right_wheel_encoder = (360/(2*Math.PI))*right_wheel_radians;

        //double left_wheel_encoder = ((360.0/8400.0)*Math.abs(left_wheel_odometry)) % 360.0;
        //double right_wheel_encoder = ((360.0/8400.0)*Math.abs(right_wheel_odometry)) % 360.0;


        //System.out.println("Left_wheel_degrees: " + left_wheel_encoder + " -- Right_wheel_degrees: " + right_wheel_encoder);

        //System.out.println("Left degrees: " + left_wheel_encoder + "  --   Right degrees: " + right_wheel_encoder);

        left_wheel_odometry = (-1)*left_wheel_odometry;

        // Return odometry to library.
        return new WheelOdometry(timestamp,left_wheel_odometry,right_wheel_odometry);
    }

    @Override
    protected String descriptorString() {
        return null;
    }
}