import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Random;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Foe.java
 * This class represents a moving sprite that moves randomly.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Foe extends MovingSprite {
	public static Image[] upImages;
	public static Image[] downImages;
	public static Image[] rightImages;
	public static Image[] leftImages;
	final static double STEP_SPEED_MULTIPLIER = 0.1;

	private Random ranGen = new Random();
	
	/**
	 * Constructor for Foe objects. Chains to MovingSprite's constructor.
	 * 
	 * @param aDelegate The MovingSpriteDelegate object
	 */
	public Foe(MovingSpriteDelegate aDelegate) {
		super(aDelegate);
		
		health = 1;
		immunity = 0;
		moveDistance = 1;
	}
	
	/**
	 * Draws a foe image, based on the direction and number of steps mvoed.
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
		
		g.drawImage(imageArr[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 3], loc.x, loc.y, size.width, size.height, null);
	}
	
	/**
	 * Randomly changes the direction to move.
	 */
	public void randomizeDirection() {
		switch (ranGen.nextInt(4)) {
			case (0): moveUp(); break;
			case (1): moveDown(); break;
			case (2): moveLeft(); break;
			case (3): moveRight(); break;
		}
	}
	
	/**
	 * Moves the foe, if possible. Occasionally randomly change direction if no longer can move towards a direction
	 * or if delegate says it should.
	 */
	public void act() {
		if (direction == null || (ranGen.nextDouble() <= 0.5 && delegate.shouldChangeDirection(this))) {
			randomizeDirection();
		}
		
		Point newPoint = new Point(loc.x + distanceToMove.x, loc.y + distanceToMove.y);
		Point autoCorrectedPoint = delegate.autoCorrectedPoint(newPoint, this);
		if (autoCorrectedPoint.x != loc.x || autoCorrectedPoint.y != loc.y) {
			stepCount++;
		} else if (delegate.shouldChangeDirection(this)) {
			randomizeDirection();
		}
		loc = autoCorrectedPoint;
	}
}
