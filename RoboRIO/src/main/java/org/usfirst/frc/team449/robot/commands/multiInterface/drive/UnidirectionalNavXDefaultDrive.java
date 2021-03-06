package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.commands.PIDAngleCommand;

/**
 * Drive with arcade drive setup, and when the driver isn't turning, use a NavX to stabilize the robot's alignment.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalNavXDefaultDrive <T extends YamlSubsystem & DriveUnidirectional & SubsystemNavX> extends PIDAngleCommand {

	/**
	 * The drive this command is controlling.
	 */
	@NotNull
	protected final T subsystem;

	/**
	 * The OI giving the input stick values.
	 */
	@NotNull
	protected final OIUnidirectional oi;

	/**
	 * The maximum velocity for the robot to be at in order to switch to driveStraight, in degrees/sec
	 */
	private final double maxAngularVelToEnterLoop;

	/**
	 * A bufferTimer so we only switch to driving straight when the conditions are met for a certain period of time.
	 */
	@NotNull
	private final BufferTimer driveStraightLoopEntryTimer;

	/**
	 * Whether or not we should be using the NavX to drive straight stably.
	 */
	private boolean drivingStraight;

	/**
	 * Default constructor
	 *
	 * @param toleranceBuffer             How many consecutive loops have to be run while within tolerance to be
	 *                                    considered on target. Multiply by loop period of ~20 milliseconds for time.
	 *                                    Defaults to 0.
	 * @param absoluteTolerance           The maximum number of degrees off from the target at which we can be
	 *                                    considered within tolerance.
	 * @param minimumOutput               The minimum output of the loop. Defaults to zero.
	 * @param maximumOutput               The maximum output of the loop. Can be null, and if it is, no maximum output
	 *                                    is used.
	 * @param deadband                    The deadband around the setpoint, in degrees, within which no output is given
	 *                                    to the motors. Defaults to zero.
	 * @param maxAngularVelToEnterLoop    The maximum angular velocity, in degrees/sec, at which the loop will be
	 *                                    entered. Defaults to 180.
	 * @param inverted                    Whether the loop is inverted. Defaults to false.
	 * @param kP                          Proportional gain. Defaults to zero.
	 * @param kI                          Integral gain. Defaults to zero.
	 * @param kD                          Derivative gain. Defaults to zero.
	 * @param driveStraightLoopEntryTimer The buffer timer for starting to drive straight.
	 * @param subsystem                   The drive to execute this command on.
	 * @param oi                          The OI controlling the robot.
	 */
	@JsonCreator
	public UnidirectionalNavXDefaultDrive(@JsonProperty(required = true) double absoluteTolerance,
	                                      int toleranceBuffer,
	                                      double minimumOutput, @Nullable Double maximumOutput,
	                                      double deadband,
	                                      @Nullable Double maxAngularVelToEnterLoop,
	                                      boolean inverted,
	                                      int kP,
	                                      int kI,
	                                      int kD,
	                                      @NotNull @JsonProperty(required = true) BufferTimer driveStraightLoopEntryTimer,
	                                      @NotNull @JsonProperty(required = true) T subsystem,
	                                      @NotNull @JsonProperty(required = true) OIUnidirectional oi) {
		//Assign stuff
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, subsystem, kP, kI, kD);
		this.oi = oi;
		this.subsystem = subsystem;

		this.driveStraightLoopEntryTimer = driveStraightLoopEntryTimer;
		this.maxAngularVelToEnterLoop = maxAngularVelToEnterLoop != null ? maxAngularVelToEnterLoop : 180;

		//Needs a requires because it's a default command.
		requires(this.subsystem);

		//Logging, but in Spanish.
		Logger.addEvent("Drive Robot bueno", this.getClass());
	}

	/**
	 * Initialize PIDController and variables.
	 */
	@Override
	protected void initialize() {
		//Reset all values of the PIDController and enable it.
		this.getPIDController().reset();
		this.getPIDController().enable();
		Logger.addEvent("UnidirectionalNavXArcadeDrive init.", this.getClass());

		//Initial assignment
		drivingStraight = false;
	}

	/**
	 * Decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	protected void execute() {
		//Check whether we're commanding to drive straight or turn.
		boolean commandingStraight = oi.commandingStraight();

		//If we're driving straight but the driver tries to turn or overrides the navX:
		if (drivingStraight && (!commandingStraight || subsystem.getOverrideNavX())) {
			//Switch to free drive
			drivingStraight = false;
			Logger.addEvent("Switching to free drive.", this.getClass());
		}
		//If we're free driving and the driver stops turning:
		else if (driveStraightLoopEntryTimer.get(!(subsystem.getOverrideNavX()) && !(drivingStraight) &&
				commandingStraight && Math.abs(subsystem.getNavX().getRate()) <= maxAngularVelToEnterLoop)) {
			//Switch to driving straight
			drivingStraight = true;
			//Set the setpoint to the current heading and reset the navX
			this.getPIDController().reset();
			this.getPIDController().setSetpoint(subsystem.getGyroOutput());
			this.getPIDController().enable();
			Logger.addEvent("Switching to DriveStraight.", this.getClass());
		}
	}

	/**
	 * Run constantly because this is a defaultDrive
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("UnidirectionalNavXArcadeDrive End.", this.getClass());
	}

	/**
	 * Stop the motors and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("UnidirectionalNavXArcadeDrive Interrupted! Stopping the robot.", this.getClass());
		subsystem.fullStop();
	}

	/**
	 * Give the correct output to the motors based on whether we're in free drive or drive straight.
	 *
	 * @param output The output of the angular PID loop.
	 */
	@Override
	protected void usePIDOutput(double output) {
		//If we're driving straight..
		if (drivingStraight) {
			//Process the output (minimumOutput, deadband, etc.)
			output = processPIDOutput(output);

			//Adjust the heading according to the PID output, it'll be positive if we want to go right.
			subsystem.setOutput(oi.getLeftOutput() - output, oi.getRightOutput() + output);
		}
		//If we're free driving...
		else {
			//Set the throttle to normal arcade throttle.
			subsystem.setOutput(oi.getLeftOutput(), oi.getRightOutput());
		}
	}
}