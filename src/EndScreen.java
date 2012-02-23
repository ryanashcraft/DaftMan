import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.Timer;


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

public class EndScreen extends JPanel implements ActionListener, KeyListener, MouseListener {
	private EndScreenDelegate delegate;
	private JLabel wonLabel;
	private JLabel scoreLabel;
	private JLabel bonusLabel;
	private JLabel totalScoreLabel;
	private JLabel recordDirectionsLabel;
	private JLabel nameLabel;
	
	private Timer timer;
	private final int TIME_SHOWN = 10000;
	
	private boolean won;
	private int totalScore;
	private int lastLevelPlayed;
	private int health;
	
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
	public EndScreen(Dimension aDimension, boolean didWin, int score, int timeLeft, int aHealth, int levelPlayed,EndScreenDelegate aDelegate) {
		delegate = aDelegate;
		
		won = didWin;
		totalScore = score + timeLeft;
		lastLevelPlayed = levelPlayed;
		health = aHealth;
		
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		
		setBackground(Color.BLACK);
		setSize(aDimension);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		String str;
		if (won) {
			str = Game.addExtraSpaces(String.format("Completed Level %02d", levelPlayed));
		} else {
			str = Game.addExtraSpaces("Game Over!");
		}
		
		wonLabel = new JLabel(str);
		wonLabel.setHorizontalAlignment(JLabel.CENTER);
		wonLabel.setFont(Game.font);
		wonLabel.setForeground(Color.WHITE);
		add(wonLabel);
	
		scoreLabel = new JLabel("Score    " + score);
		scoreLabel.setHorizontalAlignment(JLabel.CENTER);
		scoreLabel.setFont(Game.font);
		scoreLabel.setForeground(Color.WHITE);
		add(scoreLabel);
		
		if (won) {
			bonusLabel = new JLabel(Game.addExtraSpaces("Time Bonus " + timeLeft));
			bonusLabel.setHorizontalAlignment(JLabel.CENTER);
			bonusLabel.setFont(Game.font);
			bonusLabel.setForeground(Color.WHITE);
			add(bonusLabel);
		
			totalScoreLabel = new JLabel(Game.addExtraSpaces("Total " + totalScore));
			totalScoreLabel.setHorizontalAlignment(JLabel.CENTER);
			totalScoreLabel.setFont(Game.font);
			totalScoreLabel.setForeground(Color.WHITE);
			add(totalScoreLabel);
		} else {
			recordDirectionsLabel = new JLabel(Game.addExtraSpaces("Enter Name And Press Enter"));
			recordDirectionsLabel.setHorizontalAlignment(JLabel.CENTER);
			recordDirectionsLabel.setFont(Game.font);
			recordDirectionsLabel.setForeground(Color.WHITE);
			add(recordDirectionsLabel);
			
			nameLabel = new JLabel();
			nameLabel.setHorizontalAlignment(JLabel.CENTER);
			nameLabel.setFont(Game.font);
			nameLabel.setForeground(Color.WHITE);
			add(nameLabel);
		}
		
		int startingOffset = -(int)Game.font.getSize2D()*2;
		
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

			timer = new Timer(TIME_SHOWN, this);
			timer.start();
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
		
		delegate.startSequencer();
	}
	
	/**
	 * Required ActionListener method. Called when timer fires.
	 * 
	 * @param e The ActionEvent object
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			if (won) {
				delegate.newLevel(totalScore, lastLevelPlayed+1, health);
			}
		}
	}
	
	/**
	 * Stops the timer, if running.
	 */
	public void stop() {
		if (timer != null && timer.isRunning()) {
			timer.stop();
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
			delegate.recordScore(totalScore, name.trim());
			delegate.showMainMenu();
		}
		
		nameLabel.setText(Game.addExtraSpaces(name));
	}

	
	/**
	 * Required, but unused KeyListener methods. See API for more information.
	 * @param e The KeyEvent object
	 */
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
	
	/**
	 * Called when user presses a mouse button. Requests focus in window.
	 * 
	 * @param e The MouseEvent object
	 */
	public void mousePressed(MouseEvent e) {
		this.requestFocusInWindow();
	}
	
	/**
	 * Required, but unused MouseListener methods. See API for more information.
	 * @param e The MouseEvent object
	 */
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) {}
}

/**
 * EndScreenDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
interface EndScreenDelegate {
	/**
	 * Shows the main menu.
	 */
	public void showMainMenu();
	/**
	 * Records the score.
	 * 
	 * @param totalScore The score value
	 * @param text The name of the scorer
	 */
	public void recordScore(int totalScore, String name);
	/**
	 * Starts the MIDI sequencer.
	 */
	public void startSequencer();
	/**
	 * Called when the level is over to start a new level.
	 * 
	 * @param score The score
	 * @param level The level just played
	 * @param health The health
	 */
	public void newLevel(int score, int level, int health);
}