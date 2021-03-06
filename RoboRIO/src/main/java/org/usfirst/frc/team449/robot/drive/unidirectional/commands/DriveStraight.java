package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.oi.unidirectional.tank.OITank;

/**
 * Drives straight when using a tank drive.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveStraight <T extends YamlSubsystem & DriveUnidirectional> extends YamlCommandWrapper {

	/**
	 * The oi that this command gets input from.
	 */
	@NotNull
	private final OITank oi;

	/**
	 * Whether to use the left or right joystick for the forward velocity.
	 */
	private final boolean useLeft;

	/**
	 * The drive subsystem to execute this command on.
	 */
	@NotNull
	private final T subsystem;

	/**
	 * The throttle gotten from the joystick. This is a field instead of a local variable to avoid garbage collection.
	 */
	private double throttle;

	/**
	 * Drive straight without NavX stabilization.
	 *
	 * @param subsystem The drive subsystem to execute this command on.
	 * @param oi        The oi to get input from.
	 * @param useLeft   true to use the left stick to drive straight, false to use the right.
	 */
	@JsonCreator
	public DriveStraight(@NotNull @JsonProperty(required = true) T subsystem,
	                     @NotNull @JsonProperty(required = true) OITank oi,
	                     @JsonProperty(required = true) boolean useLeft) {
		this.subsystem = subsystem;
		this.oi = oi;
		this.useLeft = useLeft;
		requires(subsystem);
		Logger.addEvent("Drive Robot bueno", this.getClass());
	}

	/**
	 * Stop the drive for safety reasons.
	 */
	@Override
	protected void initialize() {
		subsystem.fullStop();
	}

	/**
	 * Give output to the motors based on the joystick input.
	 */
	@Override
	protected void execute() {
		if (useLeft) {
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}

		subsystem.setOutput(throttle, throttle);
	}

	/**
	 * Runs constantly because this is a drive command.
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Log and brake when interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DriveStraight Interrupted! Stopping the robot.", this.getClass());
		subsystem.fullStop();
	}
}
