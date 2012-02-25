import java.awt.Graphics;
import java.awt.Image;

import core.ImageStore;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Fire.java
 * This class represents a fire.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Fire extends Sprite {
	private static Image[] fireImages;
	
	protected FireDelegate delegate;
	final double STEP_SPEED_MULTIPLIER = 0.1;
	
	/**
	 * Constructor for Fire objects.
	 * 
	 * @param aDelegate The FireDelegate object
	 */
	public Fire(FireDelegate aDelegate) {
		delegate = aDelegate;
		
		fireImages = ImageStore.get().getAnimation("FIRE");
	}

	/**
	 * Draws a fire image.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.drawImage(fireImages[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 2], loc.x, loc.y, size.width, size.height, null);
	}

	/**
	 * Eventually tells delegate to stop the fire after a number of steps.
	 */
	public void act() {	
		stepCount++;
		
		if (SceneDirector.getInstance().secondsToCycles(1) == stepCount) {
			delegate.stopFire(this);
		}
	}
}

/**
 * FireDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
interface FireDelegate {
	/**
	 * Stops the fire.
	 * 
	 * @param aFire The fire to stop
	 */
	public void stopFire(Fire aFire);
}