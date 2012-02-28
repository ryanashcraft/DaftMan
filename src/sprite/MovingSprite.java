package sprite;

import java.awt.Point;

import scene.SceneDirector;

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
	private int moveDistance;
	private Point distanceToMove;
	private SpriteDirection direction;
	
	private int health;
	private int immunity;

	private int pauseTime;
	
	/**
	 * Constructor for MovingSprite objects
	 * 
	 * @param aDelegate
	 */
	public MovingSprite(MovingSpriteDelegate aDelegate, int health, int moveDistance) {
		delegate = aDelegate;
		
		this.health = health;
		this.moveDistance = moveDistance;
		
		distanceToMove = new Point(0, 0);
	}
	
	/**
	 * Sets moving direction to up.
	 */
	public void moveUp() {
		distanceToMove = new Point(0, -moveDistance);
		direction = SpriteDirection.UP;
	}
	
	/**
	 * Sets moving direction to down.
	 */
	public void moveDown() {
		distanceToMove = new Point(0, moveDistance);
		direction = SpriteDirection.DOWN;
	}
	
	/**
	 * Sets moving direction to left.
	 */
	public void moveLeft() {
		distanceToMove = new Point(-moveDistance, 0);
		direction = SpriteDirection.LEFT;
	}
	
	/**
	 * Sets moving direction to right.
	 */
	public void moveRight() {
		distanceToMove = new Point(moveDistance, 0);
		direction = SpriteDirection.RIGHT;
	}
	
	public void stopMoving() {
		distanceToMove = new Point(0, 0);
		direction = SpriteDirection.STOP;
	}
	
	public boolean isPaused() {
		return pauseTime != 0;
	}

	public void setPauseTime(int pauseTime) {
		this.pauseTime = pauseTime;
	}

	/**
	 * Set moving direction to SpriteDirection parameter.
	 * @parameter direction Direction to move
	 */
	public void move(SpriteDirection direction) {
		if (direction == SpriteDirection.DOWN) {
			moveDown();
		} else if (direction == SpriteDirection.UP) {
			moveUp();
		} else if (direction == SpriteDirection.RIGHT) {
			moveRight();
		} else if (direction == SpriteDirection.LEFT) {
			moveLeft();
		} else if (direction == SpriteDirection.STOP) {
			stopMoving();
		}
	}
	
	/**
	 * Moves with each step. Points are autocorrected to avoid dangerous collisions with impassable tiles.
	 */
	public void act() {
		if (isPaused()) {
			--pauseTime;

			return;
		}

		if (health <= 0) {
			return;
		}
		
		if (getImmunity() > 0) {
			--immunity;
		}
		
		Point newPoint = new Point(loc.x + distanceToMove.x, loc.y + distanceToMove.y);
		Point autoCorrectedPoint = delegate.autoCorrectedPoint(newPoint, this);
		if (autoCorrectedPoint.x != loc.x || autoCorrectedPoint.y != loc.y || !delegate.isTileSafe(delegate.tileForPoint(autoCorrectedPoint))) {
			stepCount++;
		} else {
			stopMoving();
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
	
	public SpriteDirection getDirection() {
		return direction;
	}
	
	public int getImmmunity() {
		return immunity;
	}
	
	public int getMoveDistance() {
		return moveDistance;
	}
	
	public void setMoveDistance(int moveDistance) {
		this.moveDistance = moveDistance;
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
			immunity = SceneDirector.getInstance().secondsToCycles(1);
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
	
	/**
	 * Sets the immunity
	 * 
	 * @param immunity The new immunity
	 */
	public void setImmunity(int immunity) {
		this.immunity = immunity;
	}
	
	/**
	 * Gets the distance to move.
	 * 
	 * @return Distance to move
	 */
	public Point getDistanceToMove() {
		return distanceToMove;
	}
}