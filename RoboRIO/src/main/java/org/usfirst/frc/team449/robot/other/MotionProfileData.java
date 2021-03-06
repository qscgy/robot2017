package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.Robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Data structure containing the array of points for the MP and a method to fill the MP from a csv file
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MotionProfileData {

	/**
	 * A 2D array containing 3 values for each point- position, velocity, and delta time respectively.
	 */
	private double data[][];

	/**
	 * Whether or not the profile is inverted because we're driving it backwards.
	 */
	private boolean inverted;

	/**
	 * Default constructor
	 *
	 * @param filename The filename of the .csv with the motion profile data. The first line must be the number of other
	 *                 lines.
	 * @param inverted Whether or not the profile is inverted (would be inverted if we're driving it backwards)
	 */
	@JsonCreator
	public MotionProfileData(@NotNull @JsonProperty(required = true) String filename,
	                         @JsonProperty(required = true) boolean inverted) {
		this.inverted = inverted;
		try {
			readFile(Robot.RESOURCES_PATH + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the profile from the given file and store it in data.
	 *
	 * @param filename The name of the .csv file containing the motion profile data.
	 * @throws IOException if that file doesn't exist.
	 */
	private void readFile(@NotNull String filename) throws IOException {
		//Instantiate the reader
		BufferedReader br = new BufferedReader(new FileReader(filename));
		int numLines = Integer.parseInt(br.readLine());

		//Instantiate data
		data = new double[numLines][3];

		//Declare the arrays outside the loop to avoid garbage collection.
		String[] line;
		double[] tmp;

		//Iterate through each line of data.
		for (int i = 0; i < numLines; i++) {
			//split up the line
			line = br.readLine().split(",\t");
			//declare as a new double because we already put the old object it referenced in data.
			tmp = new double[3];

			//Invert the position and velocity if the profile is inverted
			if (inverted) {
				tmp[0] = -Double.parseDouble(line[0]);
				tmp[1] = -Double.parseDouble(line[1]);
			} else {
				tmp[0] = Double.parseDouble(line[0]);
				tmp[1] = Double.parseDouble(line[1]);
			}

			//Trim the end of line comma
			tmp[2] = Double.parseDouble(line[2].replace(",", ""));
			data[i] = tmp;
		}
		//Close the reader
		br.close();
	}

	/**
	 * @return A 2D array containing 3 values for each point- position, velocity, and delta time respectively.
	 */
	@NotNull
	public double[][] getData() {
		return data;
	}
}
