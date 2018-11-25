# Real-time Car

Abstract:

The objective of this project is to examine the possibility ofcreating an autonomous vehicle capable of mapping andlocalization using a 250$Lidar.
The result is vehicle which isremotely operated and provides a video stream for visualfeedback.  While driving it also is capable of drawing a map
ofthe surrounding objects relative to its current location.  A bitarray representing the map is sent to a graphical user interface,where it gets
assembled to a image and displayed.  Making thevehicle operate autonomously has not been completed.  Some research has been done and a navigational
algorithm isavailable in code.


Where to start:

Main program start with AppManager. Everything we wrote of code is in the sub-folders to sdv. With the exception of the WheeledRobot-class that was 
re-written to fit with our odometric data. Furthermore, some changes were done in RMHCSLAM-class to adjust the particle filter.
