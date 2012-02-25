import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;


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
	public static Image[] upImages;
	public static Image[] downImages;
	public static Image[] rightImages;
	public static Image[] leftImages;
	final static double STEP_SPEED_MULTIPLIER = 0.1;

	private HeuristicDelegate heuristicDelegate;
	private Random rand;
	
	/**
	 * Constructor for Foe objects. Chains to MovingSprite's constructor.
	 * 
	 * @param aDelegate The MovingSpriteDelegate object
	 */
	public Foe(MovingSpriteDelegate aDelegate, HeuristicDelegate heuristicDelegate) {
		super(aDelegate);
		
		health = 1;
		immunity = 0;
		moveDistance = 1;
		
		this.heuristicDelegate = heuristicDelegate;
		
		rand = new Random();
	}
	
	/**
	 * Draws a foe image, based on the direction and number of steps mvoed.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		Image[] imageArr;
		if (direction == SpriteDirection.UP) {
			imageArr = upImages;
		} else if (direction == SpriteDirection.DOWN) {
			imageArr = downImages;
		} else if (direction == SpriteDirection.LEFT) {
			imageArr = leftImages;
		} else {
			imageArr = rightImages;
		}
		
		g.drawImage(imageArr[(int)(stepCount * STEP_SPEED_MULTIPLIER) % 3], loc.x, loc.y, size.width, size.height, null);
	}
	
	/**
	 * Moves the foe, if possible. Occasionally randomly change direction if no longer can move towards a direction
	 * or if delegate says it should.
	 */
	public void act() {
		boolean runSearch = delegate.canSeeBro(this);
		
		if (runSearch) {
			if (delegate.shouldChangeDirection(this) || direction == SpriteDirection.STOP) {
				move(aStarSearch());
			}
		} else if (delegate.shouldChangeDirection(this) && Math.random() * 10 < 1) {
			move(SpriteDirection.values()[rand.nextInt(SpriteDirection.values().length)]);
		}
		
		super.act();
	}
	
	public SpriteDirection aStarSearch() {
		ArrayList<Tile> visited = new ArrayList<Tile>();
		ArrayList<Path> results = new ArrayList<Path>();
		
		visited.clear();
		
		PriorityQueue<Path> priorityQueue = new PriorityQueue<Path>(1, new AStarComparator(heuristicDelegate));
		
		priorityQueue.add(new Path(new State(delegate.tileForPoint(getCenter()))));
		
		System.out.println("Searching...");
		
		while (!priorityQueue.isEmpty()) {
			Path currentPath = priorityQueue.remove();
			State currentState = currentPath.getLastState();
									
			if (!visited.contains(currentState.getTile()) && currentState != null) {
				visited.add(currentState.getTile());
				results.add(currentPath);
				
				if (heuristicDelegate.isGoalState(currentState)) {
					System.out.println("Found solution");
					if (currentPath.getPathway().size() > 1) {
						return currentPath.getPathway().get(1).getDirection();
					}
				}
				
				Iterator<State> successors = delegate.getSuccessors(currentState, this);
				while (successors.hasNext()) {
					State successor = successors.next();
					priorityQueue.add(new Path(currentPath, successor));
				}
			}
		}
		
		return SpriteDirection.STOP;
	}
}
