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

import javax.swing.*;


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

public class MainMenu extends JPanel implements MouseListener, KeyListener, ActionListener {	
	private MainMenuDelegate delegate;
	private JLabel logo;
	private JLabel playGameLabel;
	private JLabel highScoresLabel;
	
	private Timer timer;
	private int stepCount;
	
	private static final Color red = new Color(200, 56, 56);
	private static final Color green = new Color(56, 200, 56);
	private static final Color blue = new Color(56, 174, 200);
	private static final Color yellow = new Color(200, 174, 56);
	
	/**
	 * Constructor for MainMenu objecs. Shows the logo, instructions.
	 * 
	 * @param aDimension The size
	 * @param aDelegate The MainMenuDelegate object
	 */
	public MainMenu(Dimension aDimension, MainMenuDelegate aDelegate) {
		delegate = aDelegate;

		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);

		setBackground(Color.BLACK);
		setSize(aDimension);
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
		
		timer = new Timer(120*4, this);
		timer.start();
	}
	
	/**
	 * Called when user presses a key. Performs specified action based on
	 * key pressed.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_M: delegate.mute(); break;
			case KeyEvent.VK_ENTER: delegate.startGame(); break;
			case KeyEvent.VK_O: delegate.openFile(); break;
			case KeyEvent.VK_H: delegate.showHighScoreView(); break;
		}
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
	public void mouseReleased(MouseEvent e) { }

	/**
	 * Required ActionListener method. Called when timer fires.
	 * 
	 * @param e The ActionEvent object
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			if (stepCount <= 0) {
				delegate.startSequencer();
			}
			
			setLogoColor(stepCount++);
		}
	}
	
	/**
	 * Sets the logo color based off of the times the timer has fired.
	 * 
	 * @param stepCount The times the timer has fired
	 */
	public void setLogoColor(int stepCount) {
		if (stepCount % 4 == 0) {
			logo.setForeground(red);
		} else if (stepCount % 4 == 1) {
			logo.setForeground(green);
		} else if (stepCount % 4 == 2) {
			logo.setForeground(blue);
		} else {
			logo.setForeground(yellow);
		}
		
		repaint();
	}
	
	/**
	 * Stops the timer.
	 */
	public void stop() {
		timer.stop();
	}
}

/**
 * MainMenuDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
interface MainMenuDelegate {
	/**
	 * Starts the game.
	 */
	public void startGame();
	/**
	 * Starts the MIDI sequencer.
	 */
	public void startSequencer();
	/**
	 * Requests to open a file.
	 */
	public void openFile();
	/**
	 * Mutes the MIDI sequencer.
	 */
	public void mute();
	/**
	 * Shows the high score view.
	 */
	public void showHighScoreView();
}