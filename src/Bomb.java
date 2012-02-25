import java.awt.Graphics;
import java.awt.Image;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Bomb.java
 * This class represents a bomb.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Bomb extends Sprite {
	public static Image[] bombImages;
	
	protected BombDelegate delegate;
	final double STEP_SPEED_MULTIPLIER = 0.05;

	/**
	 * Constructor for Bomb objects.
	 * 
	 * @param aDelegate The BombDelegate object
	 */
	public Bomb(BombDelegate aDelegate) {
		delegate = aDelegate;
	}
	
	/**
	 * Draws a bomb image. Alternates between two images.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.drawImage(bombImages[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 2], loc.x, loc.y, size.width, size.height, null);
	}
	
	/**
	 * Explodes. Tells the delegate it exploded.
	 */
	public void explode() {
		delegate.didExplode();
	}

	/**
	 * Ticks the bomb. Eventually explodes after a number of ticks.
	 */
	public void act() {
		stepCount++;
		
		if (SceneDirector.getInstance().secondsToCycles(3) == stepCount) {
			explode();
		}
	}
}

/**
 * BombDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
interface BombDelegate {
	/**
	 * Called when a bomb explodes.
	 */
	public void didExplode();
}