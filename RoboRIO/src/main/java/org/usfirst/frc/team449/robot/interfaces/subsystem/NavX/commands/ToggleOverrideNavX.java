package org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;

/**
 * Toggle whether or not to override the NavX.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleOverrideNavX extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final NavxSubsystem subsystem;

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	@JsonCreator
	public ToggleOverrideNavX(@NotNull @JsonProperty(required = true) NavxSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("OverrideNavX init", this.getClass());
	}

	/**
	 * Toggle whether or not we're overriding the NavX
	 */
	@Override
	protected void execute() {
		subsystem.setOverrideNavX(!subsystem.getOverrideNavX());
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
		Logger.addEvent("OverrideNavX end", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("OverrideNavX Interrupted!", this.getClass());
	}
}

