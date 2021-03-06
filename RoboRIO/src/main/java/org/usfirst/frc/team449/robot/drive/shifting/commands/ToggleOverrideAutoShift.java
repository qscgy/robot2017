package org.usfirst.frc.team449.robot.drive.shifting.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShifting;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.logger.Logger;

/**
 * Override or unoverride whether we're autoshifting. Used to stay in low gear for pushing matches and more!
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleOverrideAutoShift extends YamlCommandWrapper {

	/**
	 * The drive subsystem to execute this command on.
	 */
	@NotNull
	private final DriveShifting subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive The drive subsystem to execute this command on.
	 */
	@JsonCreator
	public ToggleOverrideAutoShift(@NotNull @JsonProperty(required = true) DriveShifting drive) {
		subsystem = drive;
	}

	/**
	 * Log on initialization
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("OverrideAutoShift init", this.getClass());
	}

	/**
	 * Toggle overriding autoshifting.
	 */
	@Override
	protected void execute() {
		//Set whether or not we're overriding
		subsystem.setOverrideAutoshift(!subsystem.getOverrideAutoshift());
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
	 * Log when this command ends.
	 */
	@Override
	protected void end() {
		Logger.addEvent("OverrideAutoShift end", this.getClass());
	}

	/**
	 * Log when interrupted
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("OverrideAutoShift Interrupted!", this.getClass());
	}
}

