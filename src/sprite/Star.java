package sprite;

import java.awt.Graphics;
import java.awt.Image;

import core.ImageStore;

/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Star.java
 * This class represents a star.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Star extends Sprite {
	private static Image starImage;
	
	/**
	 * Star Constructor
	 */
	public Star() {
		starImage = ImageStore.get().getImage("STAR");
	}
	
	/**
	 * Draws a star image.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.drawImage(starImage, loc.x, loc.y, size.width, size.height, null);
	}
}
