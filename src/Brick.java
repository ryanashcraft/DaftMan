import java.awt.Graphics;
import java.awt.Image;

import core.ImageStore;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Brick.java
 * This class represents a brick.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Brick extends Wall {
	private static Image brickImage;
	
	private Sprite prize;

	/**
	 * Constructor for Brick objects. Chains to a superclass's constructor.
	 * 
	 * @param aRow The row which the wall is in
	 * @param aCol The column which the wall is in
	 * @param aPrize The prize the wall contains
	 */
	public Brick(int aRow, int aCol, Sprite aPrize) {
		super(aRow, aCol);
		
		brickImage = ImageStore.get().getImage("BRICK");
		prize = aPrize;
	}

	/**
	 * Paints a brick image.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		g.drawImage(brickImage, col * size.width, row * size.height, getSize().width, getSize().height, null);
	}
	
	/**
	 * Returns whether a brick is destructible.
	 * 
	 * @return Whether a brick is destructible
	 */
	public boolean isDestructible() {
		return true;
	}
	
	/**
	 * The prize the wall contains.
	 * 
	 * @return The prize
	 */
	public Sprite getPrize() {
		return prize;
	}
}
