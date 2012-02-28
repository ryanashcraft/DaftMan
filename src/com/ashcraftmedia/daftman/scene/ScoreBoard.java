package com.ashcraftmedia.daftman.scene;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import com.ashcraftmedia.daftman.core.DaftMan;
import com.ashcraftmedia.daftman.core.ImageStore;



/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Scoreboard.java
 * This class represents a scoreboard to display relevant information during gameplay.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class ScoreBoard {
	private Image smallHeartImage;
	
	private Point loc;
	private Dimension size;
	
	private int seconds;
	private int health;
	private int score;
	private int rupeesLeft;
	private int level;
	
	/**
	 * Constructor for ScoreBoard objects.
	 * 
	 * @param aLoc The origin location
	 * @param aSize The size
	 */
	public ScoreBoard(Point aLoc, Dimension aSize) {
		loc = aLoc;
		size = aSize;
		
		smallHeartImage = ImageStore.get().getImage("SMALL_HEART");
	}
	
	/**
	 * Draws strings and images to display important information during gameplay.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {		
		g.setColor(Color.BLACK);
		g.fillRect(loc.x, loc.y, size.width, size.height);
		
		final int OUTER_MARGIN = 16;
		FontMetrics fm = g.getFontMetrics(DaftMan.font);
		
		g.setFont(DaftMan.font);
		g.setColor(Color.WHITE);
		
		String levelString = DaftMan.addExtraSpaces(String.format("Level %02d", level));
		g.drawString(levelString, loc.x + (size.width - fm.stringWidth(levelString)) / 2, loc.y + OUTER_MARGIN*2);

		String scoreString = DaftMan.addExtraSpaces(String.format("Score %02d", score));
		g.drawString(scoreString, loc.x + OUTER_MARGIN, loc.y + OUTER_MARGIN*2);
								
		for (int i = 0; i < health; i++) {
			g.drawImage(smallHeartImage, loc.x + OUTER_MARGIN + (smallHeartImage.getWidth(null)+1)*i,
					loc.y + size.height - OUTER_MARGIN - smallHeartImage.getHeight(null),
					smallHeartImage.getWidth(null),
					smallHeartImage.getHeight(null),
					null);
		}
		
		String timeString = DaftMan.addExtraSpaces(String.format("Time %02d", seconds));
		g.drawString(timeString, size.width + loc.x*2 - OUTER_MARGIN - fm.stringWidth(timeString), loc.y + OUTER_MARGIN*2);

		String rupeesLeftString = DaftMan.addExtraSpaces(String.format("Rupees %02d", rupeesLeft));
		g.drawString(rupeesLeftString, size.width + loc.x*2 - OUTER_MARGIN - fm.stringWidth(rupeesLeftString), loc.y + OUTER_MARGIN*2 + fm.getHeight());
	}
	
	/**
	 * Sets the time.
	 * 
	 * @param aSeconds The new time (in seconds)
	 */
	public void setTime(int aSeconds) {
		seconds = aSeconds;
	}
	
	/**
	 * Sets the health.
	 * 
	 * @param aHealth The health of the bro
	 */
	public void setHealth(int aHealth) {
		health = aHealth;
	}
	
	/**
	 * Sets the score.
	 * 
	 * @param aScore The score
	 */
	public void setScore(int aScore) {
		score = aScore;
	}
	
	/**
	 * Sets the number of rupees left.
	 *  
	 * @param rupeeCount The number of rupees left
	 */
	public void setRupeesLeft(int rupeeCount) {
		rupeesLeft = rupeeCount;
	}
	
	/**
	 * Sets the level being played.
	 * 
	 * @param aLevel The level being played
	 */
	public void setLevel(int aLevel) {
		level = aLevel;
	}
}
