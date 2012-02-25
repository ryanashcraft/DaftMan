
public class State {
	private Tile tile;
	private SpriteDirection direction;
	private int weight;
	
	public State(Tile tile, SpriteDirection direction, int weight) {
		this.tile = tile;
		this.direction = direction;
		this.weight = weight;
	}
	
	public State(Tile tile) {
		this(tile, null, 0);
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public SpriteDirection getDirection() {
		return direction;
	}
	
	public int getWeight() {
		return weight;
	}
}
