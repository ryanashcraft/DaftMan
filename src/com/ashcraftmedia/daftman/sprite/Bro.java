package com.ashcraftmedia.daftman.sprite;

import java.awt.Graphics;
import java.awt.Image;

import com.ashcraftmedia.daftman.core.ImageStore;
import com.ashcraftmedia.daftman.scene.SceneDirector;




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
	final static double STEP_SPEED_MULTIPLIER = 0.1;
	
	private int boostSpeedStarStepCount;
		
	/**
	 * Constructor for Bro objects. Chains to MovingSprite's constructor.
	 * 
	 * @param aDelegate The MovingSpriteDelegate object
	 */
	public Bro(MovingSpriteDelegate aDelegate) {
		super(aDelegate, 3, 1);
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
		if (getDirection() == SpriteDirection.UP) {
			imageArr = ImageStore.get().getAnimation("BRO_UP");
		} else if (getDirection() == SpriteDirection.DOWN) {
			imageArr = ImageStore.get().getAnimation("BRO_DOWN");
		} else if (getDirection() == SpriteDirection.LEFT) {
			imageArr = ImageStore.get().getAnimation("BRO_LEFT");
		} else {
			imageArr = ImageStore.get().getAnimation("BRO_RIGHT");
		}
		
		if (getImmunity() == 0) {
			g.drawImage(imageArr[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 3], loc.x, loc.y, size.width, size.height, null);
		} else {
			// flicker image if recently hurt
			if (getImmunity() % 4 != 0) {
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
		
		if (getMoveDistance() > 1) {
			boostSpeedStarStepCount--;
			
			if (boostSpeedStarStepCount <= 0) {
				setMoveDistance(1);
			}
		}
	}

	/**
	 * Increases speed by doubling the distance moved with each step.
	 */
	public void boostSpeed() {
		setMoveDistance(2);
		boostSpeedStarStepCount = SceneDirector.getInstance().secondsToCycles(14);
	}
	
	/**
	 * Returns whether is sped up.
	 * 
	 * @return Whether is sped up
	 */
	public boolean isSpedUp() {
		return (getMoveDistance() > 1);
	}
	
	public void resetSpeed() {
		setMoveDistance(1);
	}
}
