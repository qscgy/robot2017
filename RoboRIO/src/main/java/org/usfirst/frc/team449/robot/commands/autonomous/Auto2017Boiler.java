package org.usfirst.frc.team449.robot.commands.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands.RunProfileTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Auto2017Boiler extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param runWallToPegProfile    The command for running the profile for going from the wall to the peg, which has
	 *                               already been loaded.
	 * @param dropGear               The command for dropping the held gear.
	 * @param dropGearSwitch         The switch deciding whether or not to drop the gear.
	 * @param allianceSwitch         The switch indicating which alliance we're on.
	 * @param runRedPegToKeyProfile  The command for moving from the peg to the key, on the red side of the field.
	 * @param runBluePegToKeyProfile The command for moving from the peg to the key, on the blue side of the field.
	 * @param spinUpShooter          The command for revving up the shooter. Can be null.
	 * @param fireShooter            The command for firing the shooter. Can be null.
	 */
	@JsonCreator
	public Auto2017Boiler(@NotNull @JsonProperty(required = true) RunLoadedProfile runWallToPegProfile,
	                      @NotNull @JsonProperty(required = true) YamlCommand dropGear,
	                      @NotNull @JsonProperty(required = true) MappedDigitalInput dropGearSwitch,
	                      @NotNull @JsonProperty(required = true) MappedDigitalInput allianceSwitch,
	                      @NotNull @JsonProperty(required = true) RunProfileTwoSides runRedPegToKeyProfile,
	                      @NotNull @JsonProperty(required = true) RunProfileTwoSides runBluePegToKeyProfile,
	                      @Nullable YamlCommand spinUpShooter,
	                      @Nullable YamlCommand fireShooter) {
		if (spinUpShooter != null) {
			addParallel(spinUpShooter.getCommand());
		}
		addSequential(runWallToPegProfile);
		if (dropGearSwitch.getStatus().get(0)) {
			addSequential(dropGear.getCommand());
		}

		//Red is true, blue is false
		if (allianceSwitch.getStatus().get(0)) {
			addSequential(runRedPegToKeyProfile);
		} else {
			addSequential(runBluePegToKeyProfile);
		}

		if (fireShooter != null) {
			addSequential(fireShooter.getCommand());
		}
	}
}
