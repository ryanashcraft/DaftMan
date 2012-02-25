import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;


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

public class Game extends JApplet {
	private static Sequencer sequencer;
	
	private static Image wallImage;
	private static Image brickImage;
	
	private static Image[] broUpImages;
	private static Image[] broDownImages;
	private static Image[] broLeftImages;
	private static Image[] broRightImages;
	private static Image[] foeUpImages;
	private static Image[] foeDownImages;
	private static Image[] foeLeftImages;
	private static Image[] foeRightImages;
	
	private static Image[] bombImages;
	private static Image[] fireImages;
	private static Image yellowRupeeImage;
	private static Image blueRupeeImage;
	private static Image heartImage;
	public static Image smallHeartImage;
	public static Image starImage;
	
	public static Font h1;
	public static Font h2;
	public static Font font;
	
	private boolean muted;
	
	/**
	 * Initializes the applet.
	 */
	public void init() {
		setSize(SceneDirector.getInstance().getContainer().getDimension());
		
		prepareResources();
		
		SceneDirector.getInstance().pushScene(new MainMenu(SceneDirector.getInstance().getContainer()));
//		showMainMenu();
		
		this.add(SceneDirector.getInstance());
	}
	
	/**
	 * Called when the applet is closed. Closes the mySQL connection, stops the timers
	 * and stops all sounds.
	 */
	public void destroy() {
		HighScoreDataCollector.getInstance().closeConnection();
		
		stopSequencer();
		if (sequencer != null) {
			if (sequencer.isOpen()) {
				sequencer.close();
			}
		}
		
		sequencer = null;
	}
	
	/**
	 * Reads all images, sounds, and fonts used in program. Also calls to retrieve
	 * high score list.
	 */
	public void prepareResources() {
		// use a media tracker to make sure all images are fully loaded
		MediaTracker mt = new MediaTracker(this);
		int imagesCount = 0;
			
		wallImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/wall.png"));
		mt.addImage(wallImage, imagesCount++);
	
		brickImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/brick.png"));
		mt.addImage(brickImage, imagesCount++);
		
		broUpImages = new Image[3];
		for (int i = 0; i < broUpImages.length; i++) {
			broUpImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/bro-up-" + (i+1) + ".png"));
			mt.addImage(broUpImages[i], imagesCount++);
		}
		
		broDownImages = new Image[3];
		for (int i = 0; i < broDownImages.length; i++) {
			broDownImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/bro-down-" + (i+1)  + ".png"));
			mt.addImage(broDownImages[i], imagesCount++);
		}
		
		broLeftImages = new Image[3];
		for (int i = 0; i < broLeftImages.length; i++) {
			broLeftImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/bro-left-" + (i+1)  + ".png"));
			mt.addImage(broLeftImages[i], imagesCount++);
		}
		
		broRightImages = new Image[3];
		for (int i = 0; i < broRightImages.length; i++) {
			broRightImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/bro-right-" + (i+1) + ".png"));
			mt.addImage(broRightImages[i], imagesCount++);
		}
		
		foeUpImages = new Image[3];
		for (int i = 0; i < foeUpImages.length; i++) {
			foeUpImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/foe-up-" + (i+1) + ".png"));
			mt.addImage(foeUpImages[i], imagesCount++);
		}
		
		foeDownImages = new Image[3];
		for (int i = 0; i < foeDownImages.length; i++) {
			foeDownImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/foe-down-" + (i+1)  + ".png"));
			mt.addImage(foeDownImages[i], imagesCount++);
		}
		
		foeLeftImages = new Image[3];
		for (int i = 0; i < foeLeftImages.length; i++) {
			foeLeftImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/foe-left-" + (i+1)  + ".png"));
			mt.addImage(foeLeftImages[i], imagesCount++);
		}
		
		foeRightImages = new Image[3];
		for (int i = 0; i < foeRightImages.length; i++) {
			foeRightImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/foe-right-" + (i+1) + ".png"));
			mt.addImage(foeRightImages[i], imagesCount++);
		}
		
		bombImages = new Image[2];
		for (int i = 0; i < bombImages.length; i++) {
			bombImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/bomb-" + (i+1) + ".png"));
			mt.addImage(bombImages[i], imagesCount++);
		}
		
		fireImages = new Image[2];
		for (int i = 0; i < fireImages.length; i++) {
			fireImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/fire-" + (i+1) + ".png"));
			mt.addImage(fireImages[i], imagesCount++);
		}
		
		yellowRupeeImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/rupee-yellow.png"));
		mt.addImage(yellowRupeeImage, imagesCount++);
		
		blueRupeeImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/rupee-blue.png"));
		mt.addImage(blueRupeeImage, imagesCount++);
		
		heartImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/heart.png"));
		mt.addImage(heartImage, imagesCount++);
		
		smallHeartImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/heart-small.png"));
		mt.addImage(smallHeartImage, imagesCount++);
		
		starImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/star.png"));
		mt.addImage(starImage, imagesCount++);
		
		try {
        	mt.waitForAll();
        } catch (InterruptedException e) {
            return;
        }
        
        Wall.wallImage = wallImage;
        Brick.brickImage = brickImage;
        
        Bro.upImages = broUpImages;
        Bro.downImages = broDownImages;
        Bro.rightImages = broRightImages;
        Bro.leftImages = broLeftImages;
        Foe.upImages = foeUpImages;
        Foe.downImages = foeDownImages;
        Foe.rightImages = foeRightImages;
        Foe.leftImages = foeLeftImages;
        
        Bomb.bombImages = bombImages;
        Fire.fireImages = fireImages;
        Rupee.yellowRupeeImage = yellowRupeeImage;
        Rupee.blueRupeeImage = blueRupeeImage;
        Heart.heartImage = heartImage;
        Star.starImage = starImage;
        
        font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("fonts/ARCADECLASSIC.TTF"));
		} catch(Exception e){
			font = new Font("Sans-Serif", Font.PLAIN, 26);
			e.printStackTrace();
		}
		
		font = font.deriveFont(Font.PLAIN, 26);
		h1 = font.deriveFont(Font.PLAIN, 72);
		h2 = font.deriveFont(Font.PLAIN, 48);
	}
	
	/**
	 * Sets the MIDI sequence to play
	 * 
	 * @param filename The file name of the MIDI sound file
	 * @param repeat Whether to repeat the MIDI file
	 * @param bpm The tempo in beats per minute for the clip 
	 * @param overlay Whether to stop previous track
	 */
	public void setSequence(String filename, int repeat, float bpm, boolean overlay) {
		if (!overlay) {
			if (sequencer != null) {
				if (sequencer.isRunning()) {
					sequencer.stop();
				}
			}
		}
		
		try {
		    Sequence sequence = MidiSystem.getSequence(getClass().getResource("sounds/" + filename));

		    sequencer = MidiSystem.getSequencer();
		    sequencer.setLoopCount(repeat);
		    if (bpm != 0.0) {
		    	sequencer.setTempoInBPM(bpm);
		    }
		    sequencer.open();
		    sequencer.setSequence(sequence);
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} catch (MidiUnavailableException e) {
		} catch (InvalidMidiDataException e) {
		}
	}
	
	/**
	 * Plays the active MIDI tracks.
	 */
	public void startSequencer() {
		if (muted) {
			return;
		}
		
		if (sequencer != null && sequencer.isOpen()) {
			if (!sequencer.isRunning()) {
				sequencer.start();
			}
		}
	}
	
	/**
	 * Stops the active MIDI tracks.
	 */
	public void stopSequencer() {
		if (sequencer != null && sequencer.isOpen()) {
			if (sequencer.isRunning()) {
				sequencer.stop();
			}
		}
	}
	
	/**
	 * Mute the active MIDI tracks.
	 * 
	 * @param toMute Whether to mute
	 */
	public void mute(boolean toMute) {
		muted = toMute;
		
		muteSounds();
	}
	
	/**
	 * Switches whether to mute the active MIDI tracks.
	 */
	public void mute() {
		muted = !muted;
		
		muteSounds();
	}
	
	/**
	 * Tells the sequencer to stop the track, which essentially
	 * mutes it.
	 */
	private void muteSounds() {
		if (muted) {
			if (sequencer != null && sequencer.isOpen()) {
				if (sequencer.isRunning()) {
					sequencer.stop();
				}
			}
		} else {
			if (sequencer != null && sequencer.isOpen()) {
				if (!sequencer.isRunning()) {
					sequencer.start();
					sequencer.setTempoInBPM(120.0f);
				}
			}
		}
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
