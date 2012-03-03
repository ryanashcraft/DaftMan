package com.ashcraftmedia.daftman.scene;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.sound.midi.Sequencer;
import javax.swing.*;

import com.ashcraftmedia.daftman.core.Container;
import com.ashcraftmedia.daftman.core.DaftMan;
import com.ashcraftmedia.daftman.core.SoundStore;
import com.ashcraftmedia.daftman.util.LevelReader;




/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * MainMenu.java
 * This class represents a main menu view.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class MainMenuScene extends Scene {	
	private JLabel logo;
	private JLabel playGameLabel;
	private JLabel highScoresLabel;
	private JLabel debugLabel;
	
	private static final Color red = new Color(200, 56, 56);
	private static final Color green = new Color(56, 200, 56);
	private static final Color blue = new Color(56, 174, 200);
	private static final Color yellow = new Color(200, 174, 56);
	
	/**
	 * Constructor for MainMenu objects. Shows the logo, instructions.
	 * 
	 * @param aDimension The size
	 * @param aDelegate The MainMenuDelegate object
	 */
	public MainMenuScene(Container container) {
		super(container);
		
		setBackground(Color.BLACK);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		logo = new JLabel("DAFTMAN");
		logo.setHorizontalAlignment(JLabel.CENTER);
		logo.setFont(DaftMan.h1);
		setLogoColor(0);
		add(logo);
		
		playGameLabel = new JLabel(DaftMan.addExtraSpaces("Press Enter To Play"));
		playGameLabel.setForeground(Color.WHITE);
		playGameLabel.setFont(DaftMan.font);
		add(playGameLabel);
		// playGameLabel.addMouseListener(this);
		
		highScoresLabel = new JLabel(DaftMan.addExtraSpaces("Press H For High Scores"));
		highScoresLabel.setForeground(Color.WHITE);
		highScoresLabel.setFont(DaftMan.font);
		add(highScoresLabel);
		
		JLabel ryanLabel = new JLabel(DaftMan.addExtraSpaces("2010-2012 Ryan Ashcraft"));
		ryanLabel.setForeground(Color.WHITE);
		ryanLabel.setFont(DaftMan.font);
		add(ryanLabel);
		
		JLabel tannerLabel = new JLabel(DaftMan.addExtraSpaces("Tanner Smith"));
		tannerLabel.setForeground(Color.WHITE);
		tannerLabel.setFont(DaftMan.font);
		add(tannerLabel);
		
		debugLabel = new JLabel(DaftMan.addExtraSpaces("Debug"));
		debugLabel.setForeground(Color.WHITE);
		debugLabel.setFont(DaftMan.font);
		debugLabel.setVisible(DaftMan.DEBUG);
		add(debugLabel);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, logo,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, logo,
                25,
                SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, playGameLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, playGameLabel,
                25,
                SpringLayout.SOUTH, logo);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, highScoresLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, highScoresLabel,
                0,
                SpringLayout.SOUTH, playGameLabel);
		
		// tanner
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, tannerLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.SOUTH, tannerLabel,
                -25,
                SpringLayout.SOUTH, this);
		
		// ryan
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, ryanLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.SOUTH, ryanLabel,
                -25,
                SpringLayout.SOUTH, tannerLabel);
		
		// debug
		layout.putConstraint(SpringLayout.WEST, debugLabel,
                7,
                SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, debugLabel,
                0,
                SpringLayout.NORTH, this);
	}
	
	/**
	 * Called when user presses a key. Performs specified action based on
	 * key pressed.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyPressed(KeyEvent e) {
		String[] level = null;
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				SceneDirector.get().pushScene(new GameScene(SceneDirector.get().getContainer()));
				break;
			case KeyEvent.VK_O:
				level = LevelReader.readLevelFile();
				SceneDirector.get().pushScene(new GameScene(SceneDirector.get().getContainer(), level));
				break;
			case KeyEvent.VK_H:
				SceneDirector.get().pushScene(new HighScoreView(SceneDirector.get().getContainer()));
				break;
			case KeyEvent.VK_M:
				SoundStore.get().mute();
				break;
			case KeyEvent.VK_B:
				if (!DaftMan.DEBUG) {
					break;
				}
				
				level = new String[11];
				for (int r = 0; r < level.length; r++) {
					if (r == 0 || r == level.length - 1) {
						level[r] = "rrrrrrrrrrrrrrr";
					} else if (r == level.length / 2) {
						level[r] = "rgggggg1ggggggr";
					} else {
						level[r] = "rgggggggggggggr";
					}
				}
				SceneDirector.get().pushScene(new GameScene(SceneDirector.get().getContainer(), level));
				break;
			case KeyEvent.VK_D:
				if (e.isShiftDown()) {
					DaftMan.DEBUG = !DaftMan.DEBUG;
					debugLabel.setVisible(DaftMan.DEBUG);
				}
				break;
		}
	}

	/**
	 * Required ActionListener method. Called when timer fires.
	 * 
	 * @param e The ActionEvent object
	 */
	public void update() {
		super.update();
		
		if (getCycleCount() <= 1) {
			SoundStore.get().playSound("AROUND_THE_WORLD", Sequencer.LOOP_CONTINUOUSLY, 120.0f, false);
		}
		
		setLogoColor(getCycleCount());
		
		debugLabel.setVisible(DaftMan.DEBUG);
	}
	
	/**
	 * Sets the logo color based off of the times the timer has fired.
	 * 
	 * @param stepCount The times the timer has fired
	 */
	public void setLogoColor(int stepCount) {
		if (stepCount % SceneDirector.get().secondsToCycles(4) == 0) {
			logo.setForeground(red);
		} else if (stepCount % SceneDirector.get().secondsToCycles(4) == SceneDirector.get().secondsToCycles(1)) {
			logo.setForeground(green);
		} else if (stepCount % SceneDirector.get().secondsToCycles(4) == SceneDirector.get().secondsToCycles(2)) {
			logo.setForeground(blue);
		} else if (stepCount % SceneDirector.get().secondsToCycles(4) == SceneDirector.get().secondsToCycles(3)) {
			logo.setForeground(yellow);
		}
		
		repaint();
	}
}