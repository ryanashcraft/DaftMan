package com.ashcraftmedia.daftman.tile;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public abstract class Tile {
	public static final Dimension size = new Dimension(32, 32);
	
	private int row;
	private int col;
	
	/**
	 * Constructor for tile objects. Decreases the row and column by
	 * one, so the board has a negative row and a negative column.
	 * 
	 * @param aRow The row of the tile
	 * @param aCol The col of the tile
	 */
	public Tile(int aRow, int aCol) {
		row = aRow-1;
		col = aCol-1;
	}
	
	/**
	 * Draws.
	 * 
	 * @param g The Graphics object
	 */
	public abstract void draw(Graphics g);
	
	public Dimension getSize() {
		return size;
	}
	
	/**
	 * Returns the origin location.
	 * 
	 * @return The origin location
	 */
	public Point getLoc() {
		return new Point(col * size.width, row * size.height);
	}
	
	/**
	 * Returns the center location.
	 * 
	 * @return The center location
	 */
	public Point getCenter() {
		return new Point(getLoc().x + size.width/2, getLoc().y + size.height/2);
	}
	
	/**
	 * Returns if the tile is impassable.
	 * 
	 * @return If the tile is impassable
	 */
	public boolean isImpassable() {
		return false;
	}
	
	/**
	 * Returns if the tile is destructible.
	 * 
	 * @return If the tile is destructible
	 */
	public boolean isDestructible() {
		return false;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public boolean isAdjacent(Tile t) {
		return Math.abs(row - t.getRow()) < 1 && Math.abs(col - t.getCol()) < 1;
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof Tile) {
			Tile other = (Tile)o;
			return other.getCenter().equals(getCenter());
		}
		
		return false;
	}
	
	public String toString() {
		return "Tile @ ("+row+", "+col+")";
	}
}
