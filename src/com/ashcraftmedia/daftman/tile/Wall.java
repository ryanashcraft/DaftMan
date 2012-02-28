package com.ashcraftmedia.daftman.tile;
import java.awt.Graphics;
import java.awt.Image;

import com.ashcraftmedia.daftman.core.ImageStore;



/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Wall.java
 * This class represents a wall tile.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Wall extends Tile {
	private static Image wallImage;

	/**
	 * Constructor for Wall objects. Chains to Tile's constructor. 
	 * 
	 * @param aRow The row of the tile
	 * @param aCol The column of the tile
	 */
	public Wall(int aRow, int aCol) {
		super(aRow, aCol);
		
		wallImage = ImageStore.get().getImage("WALL");
	}

	/**
	 * Draws a wall image.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.drawImage(wallImage, getCol() * size.width, getRow() * size.height, getSize().width, getSize().height, null);
	}
	
	/**
	 * Returns whether a brick is impassable.
	 * 
	 * @return Whether a brick is impassable
	 */
	public boolean isImpassable() {
		return true;
	}
}
