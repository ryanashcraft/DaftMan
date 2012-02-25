import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.midi.Sequencer;
import javax.swing.*;

import core.SoundStore;


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

public class MainMenu extends Scene {	
	private JLabel logo;
	private JLabel playGameLabel;
	private JLabel highScoresLabel;
	
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
	public MainMenu(Container container) {
		setBackground(Color.BLACK);
		setSize(container.getDimension());
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		logo = new JLabel("DAFTMAN");
		logo.setHorizontalAlignment(JLabel.CENTER);
		logo.setFont(Game.h1);
		setLogoColor(0);
		add(logo);
		
		playGameLabel = new JLabel(Game.addExtraSpaces("Press Enter To Play"));
		playGameLabel.setForeground(Color.WHITE);
		playGameLabel.setFont(Game.font);
		add(playGameLabel);
		// playGameLabel.addMouseListener(this);
		
		highScoresLabel = new JLabel(Game.addExtraSpaces("Press H For High Scores"));
		highScoresLabel.setForeground(Color.WHITE);
		highScoresLabel.setFont(Game.font);
		add(highScoresLabel);
		
		JLabel copyrightLabel = new JLabel(Game.addExtraSpaces("2010 Ryan Ashcraft"));
		copyrightLabel.setForeground(Color.WHITE);
		copyrightLabel.setFont(Game.font);
		add(copyrightLabel);
		
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
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, copyrightLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.SOUTH, copyrightLabel,
                -25,
                SpringLayout.SOUTH, this);
	}
	
	/**
	 * Called when user presses a key. Performs specified action based on
	 * key pressed.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				SceneDirector.getInstance().pushScene(new GameView(SceneDirector.getInstance().getContainer()));
				break;
			case KeyEvent.VK_O:
				String[] level = LevelReader.readLevelFile();
				SceneDirector.getInstance().pushScene(new GameView(SceneDirector.getInstance().getContainer(), level));
				break;
			case KeyEvent.VK_H:
				SceneDirector.getInstance().pushScene(new HighScoreView(SceneDirector.getInstance().getContainer()));
				break;
			case KeyEvent.VK_M:
				SoundStore.get().mute();
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
	}
	
	/**
	 * Sets the logo color based off of the times the timer has fired.
	 * 
	 * @param stepCount The times the timer has fired
	 */
	public void setLogoColor(int stepCount) {
		if (stepCount % SceneDirector.getInstance().secondsToCycles(4) == 0) {
			logo.setForeground(red);
		} else if (stepCount % SceneDirector.getInstance().secondsToCycles(4) == SceneDirector.getInstance().secondsToCycles(1)) {
			logo.setForeground(green);
		} else if (stepCount % SceneDirector.getInstance().secondsToCycles(4) == SceneDirector.getInstance().secondsToCycles(2)) {
			logo.setForeground(blue);
		} else if (stepCount % SceneDirector.getInstance().secondsToCycles(4) == SceneDirector.getInstance().secondsToCycles(3)) {
			logo.setForeground(yellow);
		}
		
		repaint();
	}
}