import java.awt.Color;
import java.awt.Graphics;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Grass.java
 * This class represents a grass tile.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Grass extends Tile {
	public static Color bg;
	
	private static final Color red = new Color(200, 56, 56);
	private static final Color green = new Color(56, 200, 56);
	private static final Color blue = new Color(56, 174, 200);
	private static final Color yellow = new Color(200, 174, 56);
	
	/**
	 * Constructor for Grass objects. Chains to Tile's constructor.
	 * 
	 * @param aRow The row of the tile
	 * @param aCol The col of the tile
	 */
	public Grass(int aRow, int aCol) {
		super(aRow, aCol);
	}
	
	/**
	 * Changes the background for all tiles.
	 * 
	 * @param seconds The seconds the game has played
	 */
	public static void setBackgroundWithTime(int seconds) {
		if (seconds % 4 == 0) {
			bg = red;
		} else if (seconds % 4 == 1) {
			bg = green;
		} else if (seconds % 4 == 2) {
			bg = blue;
		} else {
			bg = yellow;
		}
	}
	
	/**
	 * Draws a rectangle of the static color.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.setColor(bg);
		g.fillRect(col * size.width, row * size.height, size.width, size.height);
	}
}
