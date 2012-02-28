import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

import core.ImageStore;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * Foe.java
 * This class represents a moving sprite that moves randomly.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Foe extends MovingSprite {
	private final static double STEP_SPEED_MULTIPLIER = 0.1;

	private static final int MAX_DOUBT = SceneDirector.getInstance().secondsToCycles(2);

	private HeuristicDelegate heuristicDelegate;
	private Random rand;
	
	private Path path;
	private int doubt;
	
	/**
	 * Constructor for Foe objects. Chains to MovingSprite's constructor.
	 * 
	 * @param aDelegate The MovingSpriteDelegate object
	 */
	public Foe(MovingSpriteDelegate aDelegate, HeuristicDelegate heuristicDelegate) {
		super(aDelegate, 1, 1);
		
		this.heuristicDelegate = heuristicDelegate;
		
		rand = new Random();
		doubt = MAX_DOUBT;
	}
	
	/**
	 * Draws a foe image, based on the direction and number of steps moved.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Image[] imageArr;
		if (this.getDirection() == SpriteDirection.UP) {
			imageArr = ImageStore.get().getAnimation("FOE_UP");
		} else if (this.getDirection() == SpriteDirection.DOWN) {
			imageArr = ImageStore.get().getAnimation("FOE_DOWN");
		} else if (this.getDirection() == SpriteDirection.LEFT) {
			imageArr = ImageStore.get().getAnimation("FOE_LEFT");
		} else {
			imageArr = ImageStore.get().getAnimation("FOE_RIGHT");
		}
		
		if (Game.DEBUG && path != null) {
			ArrayList<State> states = path.getPathway();
			Point point = null;
			
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(5));
			
			for (State state : states) {
				if (point == null) {
					point = state.getTile().getCenter();
					continue;
				}
				
				g2d.drawLine(point.x, point.y, state.getTile().getCenter().x, state.getTile().getCenter().y);
				point = state.getTile().getCenter();
			}
		}
		
		if (imageArr != null) {
			g2d.drawImage(imageArr[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 3], loc.x, loc.y, size.width, size.height, null);
		}
	}
	
	/**
	 * Moves the foe, if possible. Occasionally randomly change direction if no longer can move towards a direction
	 * or if delegate says it should.
	 */
	public void act() {
		if (isPaused()) {
			super.act();
			
			return;
		}

		boolean seesBro = delegate.canSeeBro(this);
		if (seesBro) {
			doubt = 0;
		}
		
		if (delegate.shouldChangeDirection(this)) {
			path = aStarSearch();
		}
		
		if (path != null && (seesBro || doubt++ < MAX_DOUBT)) {
			move(path.getPathway().get(1).getDirection());
		} else if (delegate.shouldChangeDirection(this) && rand.nextInt(100) < 5 || getDirection() == SpriteDirection.STOP) {
			path = null;
			
			State s = new State(delegate.tileForPoint(getCenter()));
			Iterator<State> successors = delegate.getSuccessors(s, this);
			SpriteDirection direction = SpriteDirection.STOP;
			while (successors.hasNext()) {
				if (rand.nextBoolean()) {
					State successor = successors.next();
					if (delegate.isTileSafe(successor.getTile())) {
						direction = successor.getDirection();
						break;
					}
				}
				if (successors.hasNext()) {
					successors.next();
				}
			}
			
			move(direction);
		} else {
			path = null;
		}
		
		super.act();
	}
	
	public Path aStarSearch() {
		ArrayList<Tile> visited = new ArrayList<Tile>();
		ArrayList<Path> results = new ArrayList<Path>();
		
		visited.clear();
		
		PriorityQueue<Path> priorityQueue = new PriorityQueue<Path>(1, new AStarComparator());
		
		priorityQueue.add(new Path(new State(delegate.tileForPoint(getCenter()))));
				
		while (!priorityQueue.isEmpty()) {
			Path currentPath = priorityQueue.remove();
			State currentState = currentPath.getLastState();
									
			if (!visited.contains(currentState.getTile()) && currentState != null) {
				visited.add(currentState.getTile());
				results.add(currentPath);
				
				if (heuristicDelegate.isGoalState(currentState)) {
					if (currentPath.getPathway().size() > 1) {
						return currentPath;
					}
				}
				
				Iterator<State> successors = delegate.getSuccessors(currentState, this);
				while (successors.hasNext()) {
					State successor = successors.next();
					priorityQueue.add(new Path(currentPath, successor));
				}
			}
		}
		
		return null;
	}
}
