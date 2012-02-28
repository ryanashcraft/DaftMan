package search;

import tile.Tile;

public interface HeuristicDelegate {
	public int heuristicForTile(Tile t);
	public boolean isGoalState(State currentState);
}