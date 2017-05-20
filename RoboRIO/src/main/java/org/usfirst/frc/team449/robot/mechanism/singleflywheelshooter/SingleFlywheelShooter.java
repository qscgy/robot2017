package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Class for the flywheel
 */
public class SingleFlywheelShooter extends MappedSubsystem implements Loggable, ShooterSubsystem{
	/**
	 * The flywheel's Talon
	 */
	private RotPerSecCANTalonSRX shooterTalon;

	private VictorSP feederVictor;

	private double feederThrottle;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	private ShooterState state;

	/**
	 * Throttle at which to run the shooter, from [-1, 1]
	 */
	private double shooterThrottle;

	private long spinUpTime;

	/**
	 * Construct a SingleFlywheelShooter
	 *
	 * @param map config map
	 */
	public SingleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter
			                             .SingleFlywheelShooterMap.SingleFlywheelShooter map) {
		super(map.getMechanism());
		this.map = map;
		shooterTalon = new RotPerSecCANTalonSRX(map.getShooter());

		shooterThrottle = map.getShooterThrottle();
		state = ShooterState.OFF;
		spinUpTime = (long) (map.getSpinUpTimeSecs() * 1000.);
		feederVictor = new MappedVictor(map.getFeeder());
		feederThrottle = map.getFeederThrottle();
		Logger.addEvent("Shooter F: " + shooterTalon.canTalon.getF(), this.getClass());
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
		shooterTalon.setSpeed(shooterTalon.getMaxSpeed() * sp);
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 *
	 * @param sp The speed to go at [-1, 1]
	 */
	private void setFlywheelDefaultSpeed(double sp) {
		setFlywheelPIDSpeed(sp);
	}

	private void setFeederSpeed(double sp){
		feederVictor.set(sp);
	}

	/**
	 * Init the log file on enabling
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	@Override
	public String getHeader() {
		return "speed," +
				"setpoint," +
				"error," +
				"voltage," +
				"current";
	}

	@Override
	public Object[] getData() {
		return new Object[]{shooterTalon.getSpeed(),
				shooterTalon.getSetpoint(),
				shooterTalon.getError(),
				shooterTalon.canTalon.getOutputVoltage(),
				shooterTalon.canTalon.getOutputCurrent()};
	}

	@Override
	public String getName(){
		return "shooter";
	}

	/**
	 * Turn the shooter on to a map-specified speed.
	 */
	@Override
	public void turnShooterOn() {
		shooterTalon.canTalon.enable();
		setFlywheelDefaultSpeed(shooterThrottle);
	}

	/**
	 * Turn the shooter off.
	 */
	@Override
	public void turnShooterOff() {
		setFlywheelVBusSpeed(0);
		shooterTalon.canTalon.disable();
	}

	/**
	 * Start feeding balls into the shooter.
	 */
	@Override
	public void turnFeederOn() {
		setFeederSpeed(feederThrottle);
	}

	/**
	 * Stop feeding balls into the shooter.
	 */
	@Override
	public void turnFeederOff() {
		setFeederSpeed(0);
	}

	/**
	 * Sets the state of the shooter. Only called from within commands.
	 *
	 * @param state Off, spinning up, or shooting
	 */
	@Override
	public void setShooterState(ShooterState state) {
		this.state = state;
	}

	/**
	 * Gets the shooter's state, for use in "toggle" commands.
	 *
	 * @return Off, spinning up, or shooting.
	 */
	@Override
	public ShooterState getShooterState() {
		return state;
	}

	/**
	 * How long it takes for the shooter to get up to launch speed. Should be measured experimentally.
	 *
	 * @return Time from giving the shooter a voltage to being ready to fire, in milliseconds.
	 */
	@Override
	public long getSpinUpTimeMillis() {
		return spinUpTime;
	}
}
