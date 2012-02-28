package search;

import java.util.ArrayList;

public class Path {
	private ArrayList<State> path;
	private int totalWeight, lastEdgeWeight;
	
	/**
	 * Create a path with one given Successor.
	 * 
	 * @param v A Successor to add
	 */
	public Path(State v) {
		path = new ArrayList<State>();
		
		path.add(v);
		totalWeight = 0;
		lastEdgeWeight = 0;
	}
	
	/**
	 * Create a path with an old path and a pair.
	 * 
	 * @param old Old path to start from
	 * @param t Successor to add on
	 * @param SuccessorWeight Weight of Successor
	 */
	public Path(Path old, State s) {
		path = new ArrayList<State>();
		
		if (old == null) {
			return;
		}
		
		//Deep copy of old into path
		ArrayList<State> oldPathway = old.getPathway();
		for (State Successor : oldPathway) {
			path.add(Successor);
		}
		
		if (s != null) {
			path.add(s);
		}
		
		if (path.size() > 1) {
			totalWeight = old.getTotalWeight() + s.getWeight();
			lastEdgeWeight = s.getWeight();
		} else {
			totalWeight = 0;
			lastEdgeWeight = 0;
		}
	}
	
	/**
	 * Return the total weight of the path.
	 * 
	 * @return Total weight of the path
	 */
	public int getTotalWeight() {
		return totalWeight;
	}
	
	/**
	 * Return the last State.
	 * 
	 * @return Last State
	 */
	public State getLastState() {
		if (path.size() > 1) {
			return path.get(path.size() - 1);
		} else if (path.size() == 1) {
			return path.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Return the arrayList of vertices.
	 * 
	 * @return ArrayList of vertices
	 */
	public ArrayList<State> getPathway() {
		return path;
	}
	
	/**
	 * Checks whether or not a path contains the specified Successor.
	 * 
	 * @param v Successor to check for
	 * @return Whether or not that Successor is in the path
	 */
	public boolean contains(State v) {
		return path.contains(v);
	}
}