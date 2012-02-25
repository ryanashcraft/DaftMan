import java.util.Comparator;

public class AStarComparator implements Comparator<Path> {
	
	/**
	 * Compares two paths based on A-Star
	 * 
	 * @param p1 The first path
	 * @param p2 The second path
	 * @return A negative integer, zero, or a positive integer if p1 is less than, equal to, or greater than p2
	 */
	public int compare(Path p1, Path p2) {
		return p1.getTotalWeight() - p2.getTotalWeight();
	}
}

interface HeuristicDelegate {
	public int heuristicForTile(Tile t);
	public boolean isGoalState(State currentState);
}