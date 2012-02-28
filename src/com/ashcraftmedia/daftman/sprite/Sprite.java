package com.ashcraftmedia.daftman.sprite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Sprite.java
 * This abstract class represents an object with a location and size, to be drawn.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public abstract class Sprite {
	protected Point loc;
	protected Dimension size = new Dimension(32, 32);
	protected int stepCount = 0;
	
	public abstract void draw(Graphics g);
	
	/**
	 * Return The origin location.
	 * 
	 * @return The origin location
	 */
	public Point getLoc() {
		return loc;
	}
	
	/**
	 * Changes the origin location.
	 * 
	 * @param aPoint The new origin location
	 */
	public void setLoc(Point aPoint) {
		loc = aPoint;
	}
	
	/**
	 * Returns the center location.
	 * 
	 * @return The center location
	 */
	public Point getCenter() {
		return new Point(loc.x + size.width/2, loc.y + size.height/2);
	}
	
	/**
	 * Returns the size.
	 * 
	 * @return The size
	 */
	public Dimension getSize() {
		return size;
	}
}