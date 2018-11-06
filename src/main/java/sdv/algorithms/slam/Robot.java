package sdv.algorithms.slam;

import edu.wlu.cs.levy.breezyslam.robots.WheeledRobot;

public class Robot extends WheeledRobot{

    public Robot(double wheel_radius_mm, double half_axle_length_mm){
        super(wheel_radius_mm,half_axle_length_mm);
    }

    @Override
    protected WheelOdometry extractOdometry(double timestamp, double left_wheel_odometry, double right_wheel_odometry) {
        double left_wheel_degrees = left_wheel_odometry * (360.0/8400.0);
        double right_wheel_degrees = right_wheel_odometry * (360.0/8400.0);
        double timestamp_seconds = timestamp;


        return new WheelOdometry(timestamp_seconds,left_wheel_degrees,right_wheel_degrees);
    }

    @Override
    protected String descriptorString() {
        return null;
    }
}