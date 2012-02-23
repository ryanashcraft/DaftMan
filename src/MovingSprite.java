import java.awt.Point;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * MovingSprite.java
 * This class represents a sprite that moves.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public abstract class MovingSprite extends Sprite {
	protected MovingSpriteDelegate delegate;
	protected int moveDistance;
	protected Point distanceToMove;
	protected SpriteDirection direction;
	
	protected int health;
	protected int immunity;
	
	/**
	 * Moving directions.
	 */
	public enum SpriteDirection {
		UP,
		DOWN,
		LEFT,
		RIGHT
	};
	
	/**
	 * Constructor for MovingSprite objects
	 * 
	 * @param aDelegate
	 */
	public MovingSprite(MovingSpriteDelegate aDelegate) {
		delegate = aDelegate;
		
		distanceToMove = new Point(0, 0);
	}
	
	/**
	 * Sets moving direction to up.
	 */
	public void moveUp() {
		distanceToMove = new Point(0, -moveDistance);
		direction = SpriteDirection.UP;
		
		act();
	}
	
	/**
	 * Sets moving direction to down.
	 */
	public void moveDown() {
		distanceToMove = new Point(0, moveDistance);
		direction = SpriteDirection.DOWN;

		act();
	}
	
	/**
	 * Sets moving direction to left.
	 */
	public void moveLeft() {
		distanceToMove = new Point(-moveDistance, 0);
		direction = SpriteDirection.LEFT;

		act();
	}
	
	/**
	 * Sets moving direction to right.
	 */
	public void moveRight() {
		distanceToMove = new Point(moveDistance, 0);
		direction = SpriteDirection.RIGHT;

		act();
	}
	
	/**
	 * Moves with each step. Points are autocorrected to avoid dangerous collisions with impassable tiles.
	 */
	public void act() {
		if (health <= 0) {
			return;
		}
		
		Point newPoint = new Point(loc.x + distanceToMove.x, loc.y + distanceToMove.y);
		Point autoCorrectedPoint = delegate.autoCorrectedPoint(newPoint, this);
		if (autoCorrectedPoint.x != loc.x || autoCorrectedPoint.y != loc.y) {
			stepCount++;
		}
		loc = autoCorrectedPoint;
	}
	
	/**
	 * Stop moving vertically.
	 */
	public void stopMoveY() {
		distanceToMove = new Point(distanceToMove.x, 0);
	}
	
	/**
	 * Stop moving horizontally.
	 */
	public void stopMoveX() {
		distanceToMove = new Point(0, distanceToMove.y);
	}
	
	/**
	 * Returns the health.
	 * 
	 * @return The health
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Sets the health.
	 * 
	 * @param aHealth The new health
	 */
	public void setHealth(int aHealth) {
		health = aHealth;
	}
	
	/**
	 * Decreases the health and creates some immunity to prevent being hurt again too quickly. 
	 */
	public void hurt() {
		if (immunity <= 0) {
			health--;
			immunity = GameView.secondsToSteps(2);
		}
	}

	/**
	 * Adds to the health.
	 * 
	 * @param more Health to add
	 */
	public void heal(int more) {
		health = health + more;
	}

	/**
	 * Returns the immunity.
	 * 
	 * @return The immunity
	 */
	public int getImmunity() {
		return immunity;
	}
}

/**
 * MovingSpriteDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
interface MovingSpriteDelegate {
	/**
	 * Returns the corrected point for the MovingSprite to move to, based off of
	 * the point it wants to move to after it tries to move.
	 * 
	 * @param aPoint The point the MovingSprite wants to move to
	 * @param sprite The MovingSprite that is moving
	 * @return The corrected point
	 */
	public Point autoCorrectedPoint(Point newPoint, MovingSprite sprite);
	/**
	 * Returns whether a MovingSprite can move to a point without intersecting a wall
	 * 
	 * @param aPoint The point
	 * @param aSprite The moving sprite
	 * @return Whether a MovingSprite can move to a point without intersecting a wall
	 */
	public boolean canMoveToPoint(Point yChangePoint, MovingSprite sprite);
	/**
	 * Returns whether a foe should change direction.
	 * 
	 * @param aFoe The moving foe 
	 * @return Whether a foe should change direction
	 */
	public boolean shouldChangeDirection(Foe aFoe);
}