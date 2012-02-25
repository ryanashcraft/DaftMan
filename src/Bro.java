import java.awt.Graphics;
import java.awt.Image;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Bro.java
 * This class represents a moving sprite that is controlled by the user.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Bro extends MovingSprite {
	public static Image[] upImages;
	public static Image[] downImages;
	public static Image[] rightImages;
	public static Image[] leftImages;
	final static double STEP_SPEED_MULTIPLIER = 0.1;
	
	private int boostSpeedStarStepCount;
		
	/**
	 * Constructor for Bro objects. Chains to MovingSprite's constructor.
	 * 
	 * @param aDelegate The MovingSpriteDelegate object
	 */
	public Bro(MovingSpriteDelegate aDelegate) {
		super(aDelegate);
		
		immunity = 0;
		moveDistance = 1;
	}

	/**
	 * Draws a bro image, based on the direction and number of steps mvoed.
	 * If the bro is just hurt, the image is not always drawn, which makes for a 
	 * flickering effect.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		Image[] imageArr;
		if (direction == SpriteDirection.UP) {
			imageArr = upImages;
		} else if (direction == SpriteDirection.DOWN) {
			imageArr = downImages;
		} else if (direction == SpriteDirection.LEFT) {
			imageArr = leftImages;
		} else {
			imageArr = rightImages;
		}
		
		if (immunity == 0) {
			g.drawImage(imageArr[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 3], loc.x, loc.y, size.width, size.height, null);
		} else {
			// flicker image if recently hurt
			if (immunity % 2 == 1) {
				g.drawImage(imageArr[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 3], loc.x, loc.y, size.width, size.height, null);	
			}
		}
	}
	
	/**
	 * Calls MovingSprite's act() method.
	 * Decreases immunity, if exists. Slows down after a certain number of steps, if sped up.
	 */
	public void act() {
		super.act();
		
		if (immunity > 0) {
			immunity--;
		}
		
		if (moveDistance > 1) {
			boostSpeedStarStepCount--;
			
			if (boostSpeedStarStepCount <= 0) {
				moveDistance = 1;
			}
		}
	}

	/**
	 * Increases speed by doubling the distance moved with each step.
	 */
	public void boostSpeed() {
		moveDistance = 2;
		boostSpeedStarStepCount = SceneDirector.getInstance().secondsToCycles(14);
	}
	
	/**
	 * Returns whether is sped up.
	 * 
	 * @return Whether is sped up
	 */
	public boolean isSpedUp() {
		return (moveDistance > 1);
	}
}
