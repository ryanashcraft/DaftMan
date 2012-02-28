package com.ashcraftmedia.daftman.sprite;

import java.awt.Point;
import java.util.Iterator;

import com.ashcraftmedia.daftman.search.State;
import com.ashcraftmedia.daftman.tile.Tile;




/**
 * MovingSpriteDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
public interface MovingSpriteDelegate {
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
	/**
	 * Returns the successors of a foe at a point.
	 * 
	 * @param State
	 * @param aSprite The moving sprite
	 * @return Array of points and the directions to get there.
	 */
	public Iterator<State> getSuccessors(State state, MovingSprite sprite);
	/**
	 * Gets the tile that contains a point.
	 * 
	 * @param aPoint The point
	 * @return The tile the point is in
	 */
	public Tile tileForPoint(Point aPoint);
	public boolean isTileSafe(Tile tile);
	public int distanceFromBro(Point point);
	public boolean canSeeBro(MovingSprite sprite);
}