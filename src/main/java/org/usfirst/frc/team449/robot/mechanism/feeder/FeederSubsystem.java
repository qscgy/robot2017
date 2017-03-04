package org.usfirst.frc.team449.robot.mechanism.feeder;

import edu.wpi.first.wpilibj.VictorSP;
import maps.org.usfirst.frc.team449.robot.mechanism.feeder.FeederMap;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * The auger that feeds balls into the shooter.
 */
public class FeederSubsystem extends MechanismSubsystem {

	/**
	 * The Victor motor that runs the auger
	 */
	private VictorSP victor;

	/**
	 * The percentage speed for the motor to run at, from -1 to 1.
	 */
	private double speed;

	/**
	 * Whether or not the motor is currently running.
	 */
	public boolean running;

	public FeederSubsystem(FeederMap.Feeder map) {
		super(map.getMechanism());
		speed = map.getSpeed();

		//TODO make a MappedVictor class that we can directly give a motor map.
		//Initialize the Victor
		this.victor = new VictorSP(map.getVictor().getPort());
		victor.setInverted(map.getVictor().getInverted());

		//Starts off
		running = false;
	}

	/**
	 * Turns the motor on.
	 */
	public void runVictor() {
		victor.set(speed);
		running = true;
	}

	/**
	 * Turns the motor off.
	 */
	public void stopVictor() {
		victor.set(0);
		running = false;
	}

	@Override
	protected void initDefaultCommand() {
		//Do nothing! Inheritance is dumb sometimes.
	}
}
