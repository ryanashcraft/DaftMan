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
 * HighScoreView.java
 * This class shows a view that shows all the high scores.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class HighScoreView extends Scene {	
	private JLabel titleLabel;
	private JLabel[] resultLabels;
	
	/**
	 * Constructor for HighScoreView objects. Shows labels to make high score list.
	 * 
	 * @param aDimension The size of the view
	 * @param scores The high scores values
	 * @param holders The high scorer names
	 * @param aDelegate The HighScoreViewDelegate object
	 */
	public HighScoreView(Container container) {		
		super(container);
		
		setBackground(Color.BLACK);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		int[] scores = HighScoreDataCollector.getInstance().getRecordScores();
		String[] holders = HighScoreDataCollector.getInstance().getRecordHolders();
		
		titleLabel = new JLabel(DaftMan.addExtraSpaces("High Scores"));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setFont(DaftMan.h1);
		titleLabel.setForeground(Color.WHITE);
		add(titleLabel);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, titleLabel,
				0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, titleLabel,
                25,
                SpringLayout.NORTH, this);
		
		resultLabels = new JLabel[scores.length];
		for (int i = 0; i < resultLabels.length; i++) {
			if (scores[i] == 0) {
				continue;
			}
			
			resultLabels[i] = new JLabel(DaftMan.addExtraSpaces(String.format("%s %d", holders[i], scores[i])));
			resultLabels[i].setHorizontalAlignment(JLabel.CENTER);
			resultLabels[i].setFont(DaftMan.font);
			resultLabels[i].setForeground(Color.WHITE);
			add(resultLabels[i]);
			
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, resultLabels[i],
					0,
	                SpringLayout.HORIZONTAL_CENTER, this);
			if ((i-1) >= 0) {
				layout.putConstraint(SpringLayout.NORTH, resultLabels[i],
		                0,
		                SpringLayout.SOUTH, resultLabels[i-1]);
			} else {
				layout.putConstraint(SpringLayout.NORTH, resultLabels[i],
		                25,
		                SpringLayout.SOUTH, titleLabel);
			}
		}
	}
	
	/**
	 * Called when user presses a key. Performs specified action based on
	 * key pressed.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_H:
				SceneDirector.get().popScene();
				break;
			case KeyEvent.VK_M:
				SoundStore.get().mute();
				break;
		}
	}
}