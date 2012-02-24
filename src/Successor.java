import java.awt.Point;

public class Successor {
	private Point point;
	private SpriteDirection direction;
	
	public Successor(Point point, SpriteDirection direction) {
		this.point = point;
		this.direction = direction;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public SpriteDirection getDirection() {
		return direction;
	}
}
