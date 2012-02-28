import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.Timer;


public class SceneDirector extends JComponent implements ActionListener, KeyListener, MouseListener {
	private static SceneDirector instance;
	public static final int REPAINT_DELAY = 30;
	public static final int UPDATE_DELAY = 15;
	
	private Container container;
	private Stack<Scene> scenes;
	private Timer repaintTimer;
	private Timer updateTimer;
	
	public static SceneDirector getInstance() {
		if (instance == null) {
			instance = new SceneDirector();
		}
		
		return instance;
	}
	
	private SceneDirector() {
		repaintTimer = new Timer(REPAINT_DELAY, this);
		repaintTimer.start();
		updateTimer = new Timer(UPDATE_DELAY, this);
		updateTimer.start();
		
		scenes = new Stack<Scene>();
		
		container = new Container(new Dimension(16 * 32, 100 + 12 * 32));
		
		addKeyListener(this);
		addMouseListener(this);
		setFocusable(true);
		this.requestFocus();
	}
	
	public Container getContainer() {
		return container;
	}
	
	public int secondsToCycles(int seconds) {
		return seconds * 1000 / UPDATE_DELAY;
	}
	
	public void pushScene(Scene newScene) {		
		if (!scenes.isEmpty()) {
			remove(scenes.peek());
		}
		
		scenes.push(newScene);
		add(newScene);
		newScene.start();
	}
	
	public void popScene() {
		if (!scenes.isEmpty()) {
			Scene oldScene = scenes.pop();
			remove(oldScene);
			add(scenes.peek());
			scenes.peek().resume(oldScene);
		}
	}
	
	public void popToRootScene() {
		remove(scenes.peek());
		
		Scene root = scenes.get(0);
		add(root);
		root.resume(null);
				
		scenes.removeAllElements();
		scenes.add(root);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (scenes.isEmpty()) {
			return;
		}
		
		if (e.getSource() == repaintTimer) {
			repaint();
			scenes.peek().repaint();
		} else if (e.getSource() == updateTimer) {
			scenes.peek().update();
		}
	}

	public void keyPressed(KeyEvent e) {
		if (scenes.isEmpty()) {
			return;
		}
		
		scenes.peek().keyPressed(e);
	}

	public void keyReleased(KeyEvent e) {
		if (scenes.isEmpty()) {
			return;
		}
		
		scenes.peek().keyReleased(e);
	}

	public void keyTyped(KeyEvent e) {
		if (scenes.isEmpty()) {
			return;
		}
		
		scenes.peek().keyTyped(e);
	}

	public void mouseClicked(MouseEvent e) {
		requestFocus();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		requestFocus();
		
		if (scenes.isEmpty()) {
			return;
		}
		
		scenes.peek().mouseReleased(e);
	}
}
