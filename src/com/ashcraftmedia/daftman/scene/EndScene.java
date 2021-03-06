package com.ashcraftmedia.daftman.scene;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.SpringLayout;

import com.ashcraftmedia.daftman.core.Container;
import com.ashcraftmedia.daftman.core.DaftMan;
import com.ashcraftmedia.daftman.core.SoundStore;
import com.ashcraftmedia.daftman.util.HighScoreDataCollector;




/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 * 
 * EndScreen.java
 * This class shows an end screen after a user finishes a level. If the user lost,
 * the user is prompted to enter in his/her name to be sent to the record database.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class EndScene extends Scene {
	private JLabel wonLabel;
	private JLabel scoreLabel;
	private JLabel bonusLabel;
	private JLabel totalScoreLabel;
	private JLabel recordDirectionsLabel;
	private JLabel nameLabel;
	
	private boolean won;
	private int totalScore;
	
	private String name;
	private final int MAX_NAME_LENGTH = 20;
	
	/**
	 * Constructor for EndScreen objects. Shows labels to show user information about previous level.
	 * 
	 * @param aDimension The size of the view
	 * @param didWin Whether the user won the last level
	 * @param score The score from the last level
	 * @param timeLeft The time left from the last level
	 * @param aHealth The health left from the last level
	 * @param levelPlayed The level of the last level played
	 * @param aDelegate The Endscreen delegate
	 */
	//boolean didWin, int score, int timeLeft, int aHealth, int levelPlayed,EndScreenDelegate aDelegate
	public EndScene(Container container, GameScene gameView, boolean won) {
		super(container);
		
		this.won = won;
		int score = gameView.getScore();
		totalScore = score + gameView.getTimeLeft();;
		gameView.setScore(totalScore);
		int lastLevelPlayed = gameView.getLevel();
		
		setBackground(Color.BLACK);
		setSize(container.getDimension());
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		String str;
		if (won) {
			str = DaftMan.addExtraSpaces(String.format("Completed Level %02d", lastLevelPlayed));
		} else {
			str = DaftMan.addExtraSpaces("Game Over!");
		}
		
		wonLabel = new JLabel(str);
		wonLabel.setHorizontalAlignment(JLabel.CENTER);
		wonLabel.setFont(DaftMan.font);
		wonLabel.setForeground(Color.WHITE);
		add(wonLabel);
	
		scoreLabel = new JLabel("Score    " + score);
		scoreLabel.setHorizontalAlignment(JLabel.CENTER);
		scoreLabel.setFont(DaftMan.font);
		scoreLabel.setForeground(Color.WHITE);
		add(scoreLabel);
		
		if (won) {
			bonusLabel = new JLabel(DaftMan.addExtraSpaces("Time Bonus " + gameView.getTimeLeft()));
			bonusLabel.setHorizontalAlignment(JLabel.CENTER);
			bonusLabel.setFont(DaftMan.font);
			bonusLabel.setForeground(Color.WHITE);
			add(bonusLabel);
		
			totalScoreLabel = new JLabel(DaftMan.addExtraSpaces("Total " + totalScore));
			totalScoreLabel.setHorizontalAlignment(JLabel.CENTER);
			totalScoreLabel.setFont(DaftMan.font);
			totalScoreLabel.setForeground(Color.WHITE);
			add(totalScoreLabel);
		} else {
			recordDirectionsLabel = new JLabel(DaftMan.addExtraSpaces("Enter Name And Press Enter"));
			recordDirectionsLabel.setHorizontalAlignment(JLabel.CENTER);
			recordDirectionsLabel.setFont(DaftMan.font);
			recordDirectionsLabel.setForeground(Color.WHITE);
			add(recordDirectionsLabel);
			
			nameLabel = new JLabel();
			nameLabel.setHorizontalAlignment(JLabel.CENTER);
			nameLabel.setFont(DaftMan.font);
			nameLabel.setForeground(Color.WHITE);
			add(nameLabel);
		}
		
		int startingOffset = -(int)DaftMan.font.getSize2D()*2;
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, wonLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, wonLabel,
				startingOffset,
                SpringLayout.VERTICAL_CENTER, this);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, scoreLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, scoreLabel,
                0,
                SpringLayout.SOUTH, wonLabel);
		
		if (won) {
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bonusLabel,
	                0,
	                SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, bonusLabel,
	                0,
	                SpringLayout.SOUTH, scoreLabel);
			
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, totalScoreLabel,
	                0,
	                SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, totalScoreLabel,
	                0,
	                SpringLayout.SOUTH, bonusLabel);
		}
		
		else {
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, recordDirectionsLabel,
	                0,
	                SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, recordDirectionsLabel,
	                0,
	                SpringLayout.SOUTH, scoreLabel);
			
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, nameLabel,
	                0,
	                SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, nameLabel,
	                0,
	                SpringLayout.SOUTH, recordDirectionsLabel);
		}
		
		SoundStore.get().startSequencer();
	}
	
	public void update() {
		super.update();
		
		if (getCycleCount() <= 1) {
			SoundStore.get().playSound("STRONGER", 0, 160.0f, false);
		}
		
		if (won && getCycleCount() % SceneDirector.get().secondsToCycles(10) == 0) {
			SceneDirector.get().popScene();
		}
	}
	
	/**
	 * Called when user presses a key. Performs specified action based on
	 * key pressed.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyPressed(KeyEvent e) {
		if (won) {
			return;
		}
		
		if (name == null) {
			name = new String();
		}
		
		char characterTyped = e.getKeyChar();
		
		if (name.length() < MAX_NAME_LENGTH && Character.isLetter(characterTyped)) {
			name = name + characterTyped;
		}
		
		else if (name.length() < MAX_NAME_LENGTH && name.length() > 1 && e.getKeyCode() == KeyEvent.VK_SPACE) {
			name = name + " ";
		}
		
		else if (name.length() > 0 && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {			
			name = name.substring(0, name.length()-1);
		}
		
		else if (name.length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
			HighScoreDataCollector.getInstance().recordScore(totalScore, name.trim());
			
			SceneDirector.get().popToRootScene();
		}
		
		nameLabel.setText(DaftMan.addExtraSpaces(name));
	}
}