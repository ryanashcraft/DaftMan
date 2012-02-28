package com.ashcraftmedia.daftman.core;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JApplet;

import com.ashcraftmedia.daftman.scene.MainMenuScene;
import com.ashcraftmedia.daftman.scene.SceneDirector;
import com.ashcraftmedia.daftman.util.HighScoreDataCollector;



/**
 * HW10: DAFTMAN
 * This application is a take on the classic video game "Bomberman" by Hudson Soft,
 * with some design and theme inspirations from Daft Punk. Some homages to other games are included,
 * including The Legend of Zelda, Super Mario Bros, Super Mario Kart, Sonic, and Minecraft.
 * 
 * You play by trying to destroy the bricks and collecting all the rupees in each level and avoiding
 * monsters and being hit by a bomb's explosion.
 * 
 * Extra features include: 
 *  - Fancy graphics
 *  - Infinite levels with increased difficulty
 *  - Time limit per limit
 *  - Pause/Resume in-game
 *  - Health
 *  - Stars that increase speed, Sonic/Mario Kart style
 * 	- MIDI background music sound clips
 *  - WAV sound effects
 *  - Read text files for creating board (test purposes only, not available when running in browser due to Java security permissions)
 *  - Read/write to high scores database in MySQL server
 *  
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API. I also used several websites that explained how to use
 * certain Java APIs. I sourced the website above each method or line where I used
 * code that I learned about from that website.
 *  
 * Game.java
 * This class manages the views, sounds, and high score list.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class DaftMan extends JApplet {
	public static Font h1;
	public static Font h2;
	public static Font font;
	
	public static boolean DEBUG;
	
	/**
	 * Initializes the applet.
	 */
	public void init() {
		setSize(SceneDirector.getInstance().getContainer().getDimension());
		
		prepareResources();
		
		SceneDirector.getInstance().pushScene(new MainMenuScene(SceneDirector.getInstance().getContainer()));
		
		add(SceneDirector.getInstance());
	}
	
	/**
	 * Called when the applet is closed. Closes the mySQL connection, stops the timers
	 * and stops all sounds.
	 */
	public void destroy() {
		HighScoreDataCollector.getInstance().closeConnection();
		
		SoundStore.get().closeSequencer();
		SoundStore.get().closeSoundClips();
	}
	
	/**
	 * Reads all images, sounds, and fonts used in program. Also calls to retrieve
	 * high score list.
	 */
	public void prepareResources() {        
        font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/ARCADECLASSIC.TTF"));
		} catch(Exception e){
			font = new Font("Sans-Serif", Font.PLAIN, 26);
			e.printStackTrace();
		}
		
		font = font.deriveFont(Font.PLAIN, 26);
		h1 = font.deriveFont(Font.PLAIN, 72);
		h2 = font.deriveFont(Font.PLAIN, 48);
	}
	
	/**
	 * Adds extra spaces to a string, for appearances sake.
	 * 
	 * @param str The original string
	 * @return The string with extra spaces
	 */
	public static String addExtraSpaces(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') {
				sb.append("    ");
			} else {
				sb.append(str.charAt(i));
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Removes extra spaces from a string (reversing effects of
	 * public static String addExtraSpaces(String str) method.
	 * 
	 * @param str The original string
	 * @return The string without the extra spaces
	 */
	public static String removeExtraSpaces(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (i > 0 && str.charAt(i) == ' ' && str.charAt(i-1) == ' ') {
				continue;
			} else {
				sb.append(str.charAt(i));
			}
		}
		
		return sb.toString();
	}
}
