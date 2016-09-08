
package org.usfirst.frc.team957.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.ButtonType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Relay;
/*
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    RobotDrive myRobot;
    CANTalon frontRight, frontLeft, backRight, backLeft, FeedArm;
    Joystick ohWhatJoy;
    Talon Shooter, Feeder;
    Relay BoulderRoller;
    int ShootToggle, LoopCount;
    CameraServer server;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        ohWhatJoy = new Joystick(0);
        frontRight = new CANTalon(0);
        frontLeft = new CANTalon(2);
        backRight = new CANTalon(1);
        backLeft = new CANTalon(3);
        myRobot = new RobotDrive(backLeft, frontLeft, backRight, frontRight);
        myRobot.setInvertedMotor(MotorType.kFrontRight, true);
        myRobot.setInvertedMotor(MotorType.kRearRight, true);
        Shooter = new Talon(0);
        Feeder = new Talon(1);
        Feeder.enableDeadbandElimination(true);
        Shooter.enableDeadbandElimination(true);
        FeedArm = new CANTalon(5);
        BoulderRoller = new Relay(0);
        ShootToggle = 0;
        server = CameraServer.getInstance();
        server.setQuality(5);
        server.startAutomaticCapture("cam0");
        
        //Drive end, Shooting start
        /**1 make boulder roller and feeder arm work in code
        upgrade shooter so it works with a boolean toggle style so once its pressed it runs up to full speed without being held down
        Be able to turn on basic camera
        */
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		LoopCount=0;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
    		//Drive forward
    		if(LoopCount<45)
    			myRobot.arcadeDrive(.5,0);
    		//Stop drive turn Right
    		if(LoopCount>45 &&LoopCount<65)
    			myRobot.arcadeDrive(0,.5);
    		//Stop turning
    		if(LoopCount>65)
    			myRobot.arcadeDrive(0,0);
    		//Start Shooter
    		if(LoopCount>65 &&LoopCount<95)
    			Shooter.set(1);
    		//Feed ball to shooter
    		if(LoopCount>95 &&LoopCount<125)
    			{
    			Feeder.set(-1);
    			Shooter.set(1);
    			}
    		//Stop all motors
    		if(LoopCount>125)
    			{
    			Shooter.set(0);
    			Feeder.set(0);
    			}
    		
    		
    		LoopCount++;
            break;
    	}
    }

    /**
     * This function is called periodically during operator 
     */
    public void teleopPeriodic() {
    	myRobot.arcadeDrive(1*(ohWhatJoy.getRawAxis(0)),(1*ohWhatJoy.getRawAxis(1)));
    	//myRobot.tankDrive(0,1);
    	if(ohWhatJoy.getRawButton(2))
    	Feeder.set(-1);
    	else{
    		if(ohWhatJoy.getRawButton(4))
    			Feeder.set(1);
    		else
    			Feeder.set(0);
    	}
    	//Feeder.set(ohWhatJoy.getRawAxis(2));
    	//Double ShootOnOrOff;
    	//ShootOnOrOff=(ohWhatJoy.getRawButton(2))?-1.0:0;
        //Shooter.set(ShootOnOrOff);
        Relay.Value Roller;
        Roller=(ohWhatJoy.getRawButton(1))?Relay.Value.kOn:Relay.Value.kOff;
        BoulderRoller.set(Roller);
        if(ohWhatJoy.getRawButton(5))
        	FeedArm.set(-.5);
        else{
        	if(ohWhatJoy.getRawButton(6))
            	FeedArm.set(.5);
        	else
        		FeedArm.set(0);
        	
        	
        };
        switch(ShootToggle){
	        case 0://motor is off, waiting for being pressed
	        	if(ohWhatJoy.getRawButton(3)){
	        		ShootToggle = 1;
	        		Shooter.set(-1);//Turn shooter on
	        	}
	        	break;
			 case 1://motor on, waiting for release
				 if(!(ohWhatJoy.getRawButton(3)))
					 ShootToggle = 2;
				 break;
			 case 2://Motor on looking for pressed
				 if(ohWhatJoy.getRawButton(3)){
					 ShootToggle = 3;
					 Shooter.set(0);
				 }
			 	break;
			 case 3://motor off looking for release
				 if(!(ohWhatJoy.getRawButton(3)))
					 ShootToggle = 0;
			 	break;}
        SmartDashboard.putBoolean("X Button",(ohWhatJoy.getRawButton(3)));
 
        }
//SmartDashboard.putNumber("trigger value:", ohWhatJoy.getRawAxis(3));
//left is four right is five for        
    //}
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }
    
    
}
