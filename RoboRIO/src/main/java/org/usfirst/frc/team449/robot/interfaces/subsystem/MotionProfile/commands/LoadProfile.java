package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.MPSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Loads the given profile into the subsystem, but doesn't run it.
 */
public class LoadProfile extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private MPSubsystem subsystem;

	/**
	 * The profile to execute.
	 */
	private MotionProfileData profile;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 * @param profile   The profile to run.
	 */
	public LoadProfile(MPSubsystem subsystem, MotionProfileData profile) {
		this.subsystem = subsystem;
		this.profile = profile;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("LoadProfile init.", this.getClass());
	}

	/**
	 * Load the profile.
	 */
	@Override
	protected void execute() {
		subsystem.loadMotionProfile(profile);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("LoadProfile end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("LoadProfile Interrupted!", this.getClass());
	}
}