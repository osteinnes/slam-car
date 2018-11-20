/**
 * 
 * BreezySLAM: Simple, efficient SLAM in Java
 *
 * WheeledRobot.java - Java class for wheeled robots
 *
 * Copyright (C) 2014 Simon D. Levy
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,     
 * but WITHOUT ANY WARRANTY without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this code.  If not, see <http://www.gnu.org/licenses/>.
 */


package edu.wlu.cs.levy.breezyslam.robots;

import edu.wlu.cs.levy.breezyslam.components.PoseChange;


/**
 * An abstract class for wheeled robots.  Supports computation of forward and angular 
 * poseChange based on odometry. Your subclass should should implement the
 * extractOdometry method.
 */
public abstract class WheeledRobot 
{


    /**
     * Builds a WheeledRobot object. Parameters should be based on the specifications for
     * your robot.
     * @param wheel_radius_mm radius of each odometry wheel, in meters        
     * @param half_axle_length_mm half the length of the axle between the odometry wheels, in meters  
     * @return a new WheeledRobot object
     */
    protected WheeledRobot(double wheel_radius_mm, double half_axle_length_mm)
    {
        this.wheel_radius_mm = wheel_radius_mm;     
        this.half_axle_length_mm = half_axle_length_mm;

        this.timestamp_seconds_prev = 0;
        this.left_wheel_encoder_prev = 0;
        this.right_wheel_encoder_prev = 0;
    }

    public String toString() 
    {

        return String.format("<Wheel radius=%f m Half axle Length=%f m | %s>",
                this.wheel_radius_mm, this.half_axle_length_mm, this.descriptorString());
    }

    /**
     * Computes forward and angular poseChange based on odometry.
     * @param timestamp time stamp, in whatever units your robot uses       
     * @param left_wheel_odometry odometry for left wheel, in whatever units your robot uses       
     * @param right_wheel_odometry odometry for right wheel, in whatever units your robot uses
     * @return poseChange object representing poseChange for these odometry values
     */

    public PoseChange computePoseChange( double timestamp, double left_wheel_odometry, double right_wheel_odometry)
    {
        WheelOdometry odometry = this.extractOdometry(timestamp, left_wheel_odometry, right_wheel_odometry);

        double dxy_mm = 0;
        double dtheta_degrees = 0;
        double dt_seconds = 0;

        if (this.timestamp_seconds_prev > 0)
        {
            double left_diff_degrees, right_diff_degrees;

            left_diff_degrees = (odometry.left_wheel_encoder - left_wheel_encoder_prev)*(360.0/8400.0);
            right_diff_degrees = (odometry.right_wheel_encoder - right_wheel_encoder_prev)*(360.0/8400.0);

            // Calculating change in time.
            dxy_mm =  this.wheel_radius_mm * (Math.toRadians(left_diff_degrees) + Math.toRadians(right_diff_degrees));
            dtheta_degrees = this.wheel_radius_mm / this.half_axle_length_mm * (right_diff_degrees - left_diff_degrees) * 0.6;
            dt_seconds = odometry.timestamp_seconds - this.timestamp_seconds_prev;
        }

        // Store current odometry for next time
        this.timestamp_seconds_prev = odometry.timestamp_seconds;
        this.left_wheel_encoder_prev = odometry.left_wheel_encoder;
        this.right_wheel_encoder_prev = odometry.right_wheel_encoder;

        return new PoseChange(dxy_mm, dtheta_degrees, dt_seconds);
    }

    /**
     * Extracts usable odometry values from your robot's odometry.
     * @param timestamp time stamp, in whatever units your robot uses       
     * @param left_wheel_odometry odometry for left wheel, in whatever units your robot uses       
     * @param right_wheel_odometry odometry for right wheel, in whatever units your robot uses
     * @return WheelOdometry object containing timestamp in seconds, left wheel degrees, right wheel degrees
     */
    protected abstract WheelOdometry extractOdometry(double timestamp, double left_wheel_odometry, double right_wheel_odometry);

    protected abstract String descriptorString();

    protected class WheelOdometry
    {
        public WheelOdometry(double timestamp_seconds, double left_wheel_encoder, double right_wheel_encoder)
        {
            this.timestamp_seconds = timestamp_seconds;
            this.left_wheel_encoder = left_wheel_encoder;
            this.right_wheel_encoder = right_wheel_encoder;
        }

        public double timestamp_seconds; 
        public double left_wheel_encoder;
        public double right_wheel_encoder;
    
     }


    private double wheel_radius_mm;
    private double half_axle_length_mm;

    private double timestamp_seconds_prev;
    private double left_wheel_encoder_prev;
    private double right_wheel_encoder_prev;
}        
