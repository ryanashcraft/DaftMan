package com.ashcraftmedia.daftman.sprite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import com.ashcraftmedia.daftman.core.DaftMan;
import com.ashcraftmedia.daftman.core.ImageStore;
import com.ashcraftmedia.daftman.scene.SceneDirector;
import com.ashcraftmedia.daftman.search.AStarComparator;
import com.ashcraftmedia.daftman.search.HeuristicDelegate;
import com.ashcraftmedia.daftman.search.Path;
import com.ashcraftmedia.daftman.search.State;
import com.ashcraftmedia.daftman.tile.Tile;

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

	private HeuristicDelegate heuristicDelegate;
	private Random rand;
	
	private Path path;
	private int persistence;
	private int maxPersistence;
	
	/**
	 * Constructor for Foe objects. Chains to MovingSprite's constructor.
	 * 
	 * @param aDelegate The MovingSpriteDelegate object
	 */
	public Foe(MovingSpriteDelegate aDelegate, HeuristicDelegate heuristicDelegate) {
		super(aDelegate, 1, 1);
		
		this.heuristicDelegate = heuristicDelegate;
		
		rand = new Random();
		persistence = 0;
	}
	
	/**
	 * Draws a foe image, based on the direction and number of steps moved.
	 * 
	 * @param g The Graphics object
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Image[] imageArr;
		if (path == null) {
			if (this.getDirection() == SpriteDirection.UP) {
				imageArr = ImageStore.get().getAnimation("FOE_UP");
			} else if (this.getDirection() == SpriteDirection.DOWN) {
				imageArr = ImageStore.get().getAnimation("FOE_DOWN");
			} else if (this.getDirection() == SpriteDirection.LEFT) {
				imageArr = ImageStore.get().getAnimation("FOE_LEFT");
			} else {
				imageArr = ImageStore.get().getAnimation("FOE_RIGHT");
			}
		} else {
			if (this.getDirection() == SpriteDirection.UP) {
				imageArr = ImageStore.get().getAnimation("FOE_FOLLOW_UP");
			} else if (this.getDirection() == SpriteDirection.DOWN) {
				imageArr = ImageStore.get().getAnimation("FOE_FOLLOW_DOWN");
			} else if (this.getDirection() == SpriteDirection.LEFT) {
				imageArr = ImageStore.get().getAnimation("FOE_FOLLOW_LEFT");
			} else {
				imageArr = ImageStore.get().getAnimation("FOE_FOLLOW_RIGHT");
			}
		}
		
		if (DaftMan.DEBUG && path != null) {
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
			persistence = maxPersistence;
		}
		
		if (seesBro && delegate.shouldChangeDirection(this) || persistence > 0 && delegate.shouldChangeDirection(this)) {
			path = aStarSearch();
		}
		
		if (path != null && (seesBro || persistence-- > 0)) {
			move(path.getPathway().get(1).getDirection());
		} else if (path != null || delegate.shouldChangeDirection(this) && rand.nextInt(100) < 5 || getDirection() == SpriteDirection.STOP) {
			path = null;
			
			State s = new State(delegate.tileForPoint(getCenter()));
			List<State> successors = delegate.getSuccessors(s, this);
			SpriteDirection direction = SpriteDirection.STOP;
			State randomSuccessor = null;
			while (randomSuccessor == null) {
				randomSuccessor = successors.get(rand.nextInt(successors.size()));
				if (delegate.isTileSafe(randomSuccessor.getTile())) {
					direction = randomSuccessor.getDirection();
				}
			}
			
			if (direction == SpriteDirection.STOP) {
				this.stopMoving();
			} else {
				move(direction);
			}
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
				
				List<State> successors = delegate.getSuccessors(currentState, this);
				for (State successor : successors) {
					successor.setWeight(successor.getWeight() + heuristicDelegate.heuristicForTile(successor.getTile()));
					priorityQueue.add(new Path(currentPath, successor));
				}
			}
		}
		
		return null;
	}

	public void setMaxPersistence(int maxPersistence) {
		this.maxPersistence = maxPersistence;
	}
}
