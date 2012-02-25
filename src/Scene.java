import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public abstract class Scene extends JPanel implements KeyListener {
	private int cycleCount = 0;
	
	public int getCycleCount() {
		return cycleCount;
	}
	
	public void update() {
		cycleCount++;
	}
	
	public void start() {
		revalidate();
	}
	
	public void resume() {
		revalidate();
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		
	}
}
