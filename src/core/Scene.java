package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public abstract class Scene extends JPanel implements KeyListener, MouseListener {
	private int cycleCount = 0;
	
	public Scene(Container container) {
		setSize(container.getDimension());
	}
	
	public int getCycleCount() {
		return cycleCount;
	}
	
	public void update() {
		cycleCount++;
	}
	
	public void start() {
		revalidate();
	}
	
	public void resume(Scene lastScene) {
		revalidate();
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
