package org.usfirst.frc.team449.robot.subsystem.complex.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.VictorSP;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedVictor;
import org.usfirst.frc.team449.robot.jacksonWrappers.RotPerSecCANTalon;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Loggable;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.SubsystemShooter;

/**
 * A flywheel multiSubsystem with a single flywheel and a single-motor feeder system.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShooterWithVictorFeeder extends YamlSubsystem implements Loggable, SubsystemShooter {

	/**
	 * The flywheel's Talon
	 */
	@NotNull
	private final RotPerSecCANTalon shooterTalon;

	/**
	 * The feeder's Victor
	 */
	@NotNull
	private final VictorSP feederVictor;

	/**
	 * How fast to run the feeder, from [-1, 1]
	 */
	private final double feederThrottle;

	/**
	 * Throttle at which to run the multiSubsystem, from [-1, 1]
	 */
	private final double shooterThrottle;

	/**
	 * Time from giving the multiSubsystem voltage to being ready to fire, in milliseconds.
	 */
	private final long spinUpTime;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	@NotNull
	private ShooterState state;

	/**
	 * Default constructor
	 *
	 * @param shooterTalon    The TalonSRX controlling the flywheel.
	 * @param shooterThrottle The throttle, from [-1, 1], at which to run the multiSubsystem.
	 * @param feederVictor    The VictorSP controlling the feeder.
	 * @param feederThrottle  The throttle, from [-1, 1], at which to run the feeder.
	 * @param spinUpTimeSecs  The amount of time, in seconds, it takes for the multiSubsystem to get up to speed.
	 *                        Defaults to 0.
	 */
	@JsonCreator
	public ShooterWithVictorFeeder(@NotNull @JsonProperty(required = true) RotPerSecCANTalon shooterTalon,
	                               @JsonProperty(required = true) double shooterThrottle,
	                               @NotNull @JsonProperty(required = true) MappedVictor feederVictor,
	                               @JsonProperty(required = true) double feederThrottle,
	                               double spinUpTimeSecs) {
		this.shooterTalon = shooterTalon;
		this.shooterThrottle = shooterThrottle;
		this.feederVictor = feederVictor;
		this.feederThrottle = feederThrottle;
		state = ShooterState.OFF;
		spinUpTime = (long) (spinUpTimeSecs * 1000.);
		Logger.addEvent("shooter F: " + shooterTalon.getCanTalon().getF(), this.getClass());
	}

	/**
	 * Set the flywheel's percent voltage
	 *
	 * @param sp percent voltage setpoint [-1, 1]
	 */
	private void setFlywheelVBusSpeed(double sp) {
		shooterTalon.setPercentVbus(sp);
	}

	/**
	 * Set the flywheel's percent PID velocity setpoint
	 *
	 * @param sp percent PID velocity setpoint [-1, 1]
	 */
	private void setFlywheelPIDSpeed(double sp) {
		if (shooterTalon.getMaxSpeed() == null) {
			setFlywheelVBusSpeed(sp);
			System.out.println("You're trying to set PID throttle, but the multiSubsystem talon doesn't have PID constants defined. Using voltage control instead.");
		} else {
			shooterTalon.setSpeed(shooterTalon.getMaxSpeed() * sp);
		}
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 *
	 * @param sp The velocity to go at [-1, 1]
	 */
	private void setFlywheelDefaultSpeed(double sp) {
		setFlywheelPIDSpeed(sp);
	}

	/**
	 * Set the speed of the feeder motor.
	 *
	 * @param sp The velocity to go at from [-1, 1]
	 */
	private void setFeederSpeed(double sp) {
		feederVictor.set(sp);
	}

	/**
	 * Do nothing
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 *
	 * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
	 */
	@NotNull
	@Override
	public String[] getHeader() {
		return new String[]{"speed",
				"setpoint",
				"error",
				"voltage",
				"current"};
	}

	/**
	 * Get the data this subsystem logs every loop.
	 *
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	@NotNull
	@Override
	public Object[] getData() {
		return new Object[]{shooterTalon.getSpeed(),
				shooterTalon.getSetpoint(),
				shooterTalon.getError(),
				shooterTalon.getCanTalon().getOutputVoltage(),
				shooterTalon.getCanTalon().getOutputCurrent()};
	}

	/**
	 * Get the name of this object.
	 *
	 * @return A string that will identify this object in the log file.
	 */
	@NotNull
	@Override
	public String getName() {
		return "multiSubsystem";
	}

	/**
	 * Turn the multiSubsystem on to a map-specified speed.
	 */
	@Override
	public void turnShooterOn() {
		shooterTalon.getCanTalon().enable();
		setFlywheelDefaultSpeed(shooterThrottle);
	}

	/**
	 * Turn the multiSubsystem off.
	 */
	@Override
	public void turnShooterOff() {
		setFlywheelVBusSpeed(0);
		shooterTalon.getCanTalon().disable();
	}

	/**
	 * Start feeding balls into the multiSubsystem.
	 */
	@Override
	public void turnFeederOn() {
		setFeederSpeed(feederThrottle);
	}

	/**
	 * Stop feeding balls into the multiSubsystem.
	 */
	@Override
	public void turnFeederOff() {
		setFeederSpeed(0);
	}

	/**
	 * @return The current state of the multiSubsystem.
	 */
	@NotNull
	@Override
	public ShooterState getShooterState() {
		return state;
	}

	/**
	 * @param state The state to switch the multiSubsystem to.
	 */
	@Override
	public void setShooterState(@NotNull ShooterState state) {
		this.state = state;
	}

	/**
	 * @return Time from giving the multiSubsystem voltage to being ready to fire, in milliseconds.
	 */
	@Override
	public long getSpinUpTimeMillis() {
		return spinUpTime;
	}
}
