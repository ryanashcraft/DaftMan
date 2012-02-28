package com.ashcraftmedia.daftman.search;

import com.ashcraftmedia.daftman.tile.Tile;

public interface HeuristicDelegate {
	public int heuristicForTile(Tile t);
	public boolean isGoalState(State currentState);
}