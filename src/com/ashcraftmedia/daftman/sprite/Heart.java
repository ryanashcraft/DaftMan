package com.ashcraftmedia.daftman.sprite;

import java.awt.Graphics;
import java.awt.Image;

import com.ashcraftmedia.daftman.core.ImageStore;



/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Heart.java
 * This class represents a moving sprite that is controlled by the user.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Heart extends Sprite {
	private static Image heartImage;
	
	private int value;
	
	/**
	 * Constructor for Heart objects.
	 */
	public Heart() {
		value = 1;
		
		heartImage = ImageStore.get().getImage("HEART");
	}
	
	/**
	 * Returns the value.
	 * 
	 * @return The value of the heart
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Draws a heart iamge.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.drawImage(heartImage, loc.x, loc.y, size.width, size.height, null);
	}
}
