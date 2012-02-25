import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import core.ImageStore;

/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Rupee.java
 * This class represents a rupee.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Rupee extends Sprite {
	private static Image yellowRupeeImage;
	private static Image blueRupeeImage;
	private Image rupeeImage;
	private int value;
	
	private final int DEFAULT_VALUE = 20;
	
	/**
	 * Constructor for Rupee objects.
	 */
	public Rupee() {
		value = DEFAULT_VALUE;
		
		yellowRupeeImage = ImageStore.get().getImage("YELLOW_RUPEE");
		blueRupeeImage = ImageStore.get().getImage("BLUE_RUPEE");
		
		rupeeImage = yellowRupeeImage;

		// deprecated feature
		/*
		Random ranGen = new Random();
		if (ranGen.nextDouble() < .7) {
			value = 1;
			rupeeImage = yellowRupeeImage;
		} else {
			value = 10;
			rupeeImage = blueRupeeImage;
		}
		*/
	}
	
	/**
	 * Returns the value.
	 * 
	 * @return The value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Draws a rupee image.
	 */
	public void draw(Graphics g) {
		g.drawImage(rupeeImage, loc.x, loc.y, size.width, size.height, null);
	}
}
