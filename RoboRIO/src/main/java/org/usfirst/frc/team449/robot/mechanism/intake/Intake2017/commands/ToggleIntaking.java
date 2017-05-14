package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Command for toggling the intake in and out
 */
public class ToggleIntaking extends ReferencingCommand {
	/**
	 * The intake subsystem to execute this command on
	 */
	private Intake2017 intake2017;

	/**
	 * Construct a ToggleIntakeUpDown command
	 *
	 * @param intake2017 intake subsytem to execute this command on
	 */
	public ToggleIntaking(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		Logger.addEvent("ToggleIntaking constructed", this.getClass());
	}

	/**
	 * Set the motors as appropriate
	 */
	@Override
	protected void execute() {
		if (intake2017.isIntaking) {
			intake2017.setActuatedVictor(false);
			intake2017.setFixedVictor(Intake2017.FixedIntakeMode.OFF);
		} else {
			intake2017.setActuatedVictor(true);
			intake2017.setFixedVictor(Intake2017.FixedIntakeMode.INTAKING);
		}
		intake2017.setIntaking(!intake2017.isIntaking);
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
	 * Stop the motors if the command is interrupted
	 */
	@Override
	protected void interrupted() {
		intake2017.setActuatedVictor(false);
		intake2017.setFixedVictor(Intake2017.FixedIntakeMode.OFF);
		Logger.addEvent("ToggleIntaking interrupted, stopping intake2017.", this.getClass());
	}
}
