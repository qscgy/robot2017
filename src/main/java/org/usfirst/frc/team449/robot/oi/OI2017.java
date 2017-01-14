package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXTurnToAngle;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017 extends OISubsystem{

	private Throttle leftThrottle;
	private Throttle rightThrottle;
	private JoystickButton tt90;

	public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map){
		super(map.getOi());
		this.map = map;
		Joystick rightStick = new Joystick(map.getRightStick());
		leftThrottle = new SmoothedThrottle(new Joystick(map.getLeftStick()), 1);
		rightThrottle = new SmoothedThrottle(rightStick, 1);
		tt90 = new JoystickButton(rightStick, 1);
	}

	public void mapButtons(){
		tt90.whenPressed(new NavXTurnToAngle(0.01, 0,0, 1, 90, Robot.driveSubsystem));
	}

	@Override
	public double getDriveAxisLeft() {
		return -leftThrottle.getValue();
	}

	@Override
	public double getDriveAxisRight() {
		return -rightThrottle.getValue();
	}

	@Override
	public void toggleCamera() {
		//Do Nothing!
	}

	@Override
	protected void initDefaultCommand() {
		//Inheritance is stupid sometimes.
	}
}
