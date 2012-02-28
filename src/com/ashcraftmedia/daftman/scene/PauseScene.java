package com.ashcraftmedia.daftman.scene;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.SpringLayout;

import com.ashcraftmedia.daftman.core.Container;
import com.ashcraftmedia.daftman.core.DaftMan;



public class PauseScene extends Scene {

	public PauseScene(Container container) {
		super(container);
		
		setBackground(Color.BLACK);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		JLabel pauseLabel = new JLabel("Paused");
		pauseLabel.setHorizontalAlignment(JLabel.CENTER);
		pauseLabel.setForeground(Color.WHITE);
		pauseLabel.setFont(DaftMan.font);
		add(pauseLabel);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pauseLabel,
                0,
                SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, pauseLabel,
                0,
                SpringLayout.VERTICAL_CENTER, this);
	}
	
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_P: SceneDirector.getInstance().popScene(); break;
		}
	}
}
