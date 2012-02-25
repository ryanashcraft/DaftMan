import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API.
 *  
 * GameView.java
 * This class manages and displays a grid of tiles and a number of sprite objects to
 * make up the actual game part of this application. Also manages the score and scoreboard.
 * Everything runs off of a single timer.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class GameView extends Scene implements MovingSpriteDelegate, BombDelegate, FireDelegate, HeuristicDelegate {
	private final int DEFAULT_SCORE = 0;
	private final int DEFAULT_LEVEL = 1;
	private final int DEFAULT_HEALTH = 3;

	private Bro bro;
	private ArrayList<Foe> foes = new ArrayList<Foe>();
	private Bomb bomb;
	private ArrayList<Fire> fires = new ArrayList<Fire>();
	private ArrayList<Rupee> rupees = new ArrayList<Rupee>();
	private ArrayList<Heart> hearts = new ArrayList<Heart>();
	private ArrayList<Star> stars = new ArrayList<Star>();
	
	private final int MAX_BOMB_DISTANCE = 3;
	private int score = 0;
	private int level;
	
	private Tile[][] tiles;
	private ScoreBoard scoreBoard;
	private final int SCOREBOARD_HEIGHT = 80;
	
	private ArrayList<Clip> soundEffects = new ArrayList<Clip>();
	
	private final int HURT_PUNISHMENT_SCORE_VALUE = 0;
	private final int HURT_FOE_PUNISHMENT_SCORE_VALUE = 5;
	private final int TIME_TO_WIN = 120;
	private final int LAST_STEPS = 20;
	private int timeLeft;
	private int lastStepsLeft;
	private boolean gameOver;
	
	/**
	 * Constructor for GameView objects that chains to another constructor, sending null for the string array to fille.
	 * Essentially, if this constructor is called, a level will be randomly generated
	 * 
	 * @param aDimension The size of this view
	 * @param aScore The score to start with
	 * @param aLevel The level being played
	 * @param aHealth The health to start with
	 * @param aDelegate The GameViewDelegate object
	 */
	public GameView(Container container) {
		this(container, null);
	}
	
	/**
	 * Constructor for GameView objects that creates all the tiles, sets all the starting parameters, starts the timer. If
	 * stringArray is not null, fills the board with tiles and sprites defined by those strings. If it is null, a random
	 * level will be generated.
	 * 
	 * @param aDimension The size of the view
	 * @param stringArray The string array to fill the board
	 * @param aScore The score to start with
	 * @param aLevel The level being played
	 * @param aHealth The health to start with
	 * @param aDelegate The GameViewDelegate object
	 */
	public GameView(Container container, String[] stringArray) {		
		score = DEFAULT_SCORE;
		level = DEFAULT_LEVEL;
		
		scoreBoard = new ScoreBoard(new Point(-32/2, - SCOREBOARD_HEIGHT - 32), new Dimension(32 * 17 - 32/2, SCOREBOARD_HEIGHT));
				
		setBackground(Color.BLACK);
		setSize(container.getDimension());
		
		tiles = new Tile[13][17];
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				if (r == 0 || r == tiles.length-1 || c == 0 || c == tiles[r].length-1 || (r % 2 == 0 && c % 2 == 0)) {
					tiles[r][c] = new Wall(r, c);
				}
				else {
					tiles[r][c] = new Grass(r, c);
				}	
			}
		}

		bro = new Bro(this);
		bro.setHealth(DEFAULT_HEALTH);
		
		if (stringArray == null) {
			randomlyFill();
		} else {
			fillWithArray(stringArray);
		}
		
		timeLeft = TIME_TO_WIN;
		
		update();
	}
	
	/**
	 * Randomly fills the board.
	 */
	public void randomlyFill() {
		Random ranGen = new Random();
		
		final int BASE_NUMBER_OF_RUPEES = 10;
		final int ADD_NUMBER_OF_RUPEES_PER_LEVEL = 2;
		final int MAX_NUMBER_OF_RUPEES = 30;
		final int NUMBER_OF_HEARTS = 3;
		final int NUMBER_OF_STARS = 2;
		int rupeesToAdd = Math.min(BASE_NUMBER_OF_RUPEES + (level-1)*ADD_NUMBER_OF_RUPEES_PER_LEVEL, MAX_NUMBER_OF_RUPEES);		
		for (int i = 0; i < rupeesToAdd + NUMBER_OF_HEARTS + NUMBER_OF_STARS; i++) {
			int r = -1, c = -1;
			while((r == -1 || c == -1) || tiles[r][c].isImpassable() || (r < 5 && c < 5)) {
				r = ranGen.nextInt(tiles.length-1);
				c = ranGen.nextInt(tiles[tiles.length-1].length-1);
			}
			
			Sprite aSprite;
			if (i < rupeesToAdd) {
				aSprite = new Rupee();
			} else if (i < rupeesToAdd + NUMBER_OF_HEARTS) {
				aSprite = new Heart();
			} else {
				aSprite = new Star();
			}
			
			tiles[r][c] = new Brick(r, c, aSprite);
		}
		
		bro.setLoc(tiles[1][1].getLoc());
		
		final int BASE_NUMBER_OF_FOES = 2;
		final int ADD_NUMBER_OF_FOES_PER_LEVEL = 2;
		int foesToAdd = BASE_NUMBER_OF_FOES + (level-1)*ADD_NUMBER_OF_FOES_PER_LEVEL;
		for (int i = 0; i < foesToAdd; i++) {
			Foe aFoe = new Foe(this, this);
			Tile aTile = null;

			// if can't find suitable place for foe, don't run into an infinite loop
			final int MAX_ATTEMPTS = 50;
			int attempts = 0;
			while((attempts <= MAX_ATTEMPTS) && (aTile == null || aTile.isImpassable() || !adjacentTileMoveExists(aTile) || (aTile.row < 5 && aTile.col < 5))) {
				aTile = tiles[ranGen.nextInt(tiles.length-1)][ranGen.nextInt(tiles[tiles.length-1].length-1)];
				attempts++;
			}
			aFoe.setLoc(aTile.getLoc());
			foes.add(aFoe);
		}
	}
	
	/**
	 * Fills the board, based off an array of strings, where each string represents a row.
	 * 
	 * @param stringArray The string array to fill the board
	 */
	public void fillWithArray(String[] stringArray) {
		for (int r = 0; r < tiles.length-1; r++) {			
			for (int c = 0; c < tiles[r].length-1; c++) {
				if (r == 0 || r == tiles.length-1 || c == 0 || c == tiles[r].length-1) {
					tiles[r][c] = new Wall(r, c);
				}
				
				else if (stringArray[r-1].charAt(c-1) == '1') {
					bro = new Bro(this);
					bro.setLoc(tiles[r][c].getLoc());
				}
				
				else if (stringArray[r-1].charAt(c-1) == '2') {
					Foe aFoe = new Foe(this, this);
					aFoe.setLoc(tiles[r][c].getLoc());
					foes.add(aFoe);
				}
				
				else if (stringArray[r-1].charAt(c-1) == 'w') {
					tiles[r][c] = new Wall(r, c);
				}
				
				else if (stringArray[r-1].charAt(c-1) == 'g') {
					tiles[r][c] = new Grass(r, c);
				}
				
				else if (stringArray[r-1].charAt(c-1) == 'h') {
					tiles[r][c] = new Brick(r, c, new Heart());
				}
				
				else if (stringArray[r-1].charAt(c-1) == 'r') {
					tiles[r][c] = new Brick(r, c, new Rupee());
				}
				
				else if (stringArray[r-1].charAt(c-1) == 's') {
					tiles[r][c] = new Brick(r, c, new Star());
				}
			}
		}
	}
	
	/**
	 * Paints all tiles and sprites. Also paints the scoreboard. If paused, shows pause screen.
	 * 
	 * @param g The Graphics object
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.translate(32/2, SCOREBOARD_HEIGHT + 32);
		
		scoreBoard.draw(g);
		
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				tiles[r][c].draw(g);
			}
		}
		
		for (int i = rupees.size()-1; i >= 0; i--) {
			rupees.get(i).draw(g);
		}
	
		for (int i = hearts.size()-1; i >= 0; i--) {
			hearts.get(i).draw(g);
		}
	
		for (int i = stars.size()-1; i >= 0; i--) {
			stars.get(i).draw(g);
		}
		
		if (bomb != null) {
			bomb.draw(g);
		}
		
		for (int i = foes.size()-1; i >= 0; i--) {
			foes.get(i).draw(g);
		}
		
		if (bro != null) {
			bro.draw(g);
		}
		
		if (fires != null) {
			for (int i = fires.size()-1; i >= 0; i--) {
				fires.get(i).draw(g);
			}
		}
	}

	/**
	 * Called when user presses a key. Performs specified action based on key pressed.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP: bro.moveUp(); break;
			case KeyEvent.VK_DOWN: bro.moveDown(); break;
			case KeyEvent.VK_LEFT: bro.moveLeft(); break;
			case KeyEvent.VK_RIGHT: bro.moveRight(); break;
			case KeyEvent.VK_SPACE: placeBomb(); break;
			case KeyEvent.VK_C: cheat(); break;
		}
	}

	/**
	 * Called when user releases a key. Performs specified action based on key released.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyReleased(KeyEvent e) {		
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
			bro.stopMoveY();
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			bro.stopMoveX();
		}
	}

	/**
	 * Called when timer fires and tells each sprite to do something. Calls to check
	 * collisions on all moving sprites (bro and foes), and checks to see if game should
	 * end. Also sets the scoreboard's values.
	 */
	public void update() {
		super.update();
		
		if (getCycleCount() <= 0) {
//			delegate.startSequencer();
		}
		
		if (bro != null) {
			bro.act();
		}
		
		for (int i = foes.size()-1; i >=0; i--) {
			foes.get(i).act();
		}
		
		if (bomb != null) {
			bomb.act();
		}
		
		for (int i = fires.size()-1; i >= 0; i--) {
			fires.get(i).act();
		}
		
		if (getCycleCount() != 0 && getCycleCount() % SceneDirector.getInstance().secondsToCycles(1) == 0) {
			timeLeft--;
		}
		
		scoreBoard.setTime(timeLeft);
		scoreBoard.setHealth(bro.getHealth());
		scoreBoard.setScore(score);
		scoreBoard.setRupeesLeft(rupeesLeft());
		scoreBoard.setLevel(level);
		
		Grass.setBackgroundWithTime(getCycleCount() * SceneDirector.UPDATE_DELAY / 1000);
				
		if (bro != null) {
			checkCollisions(bro);
		}
		
		for (int i = foes.size()-1; i >= 0; i--) {
			checkCollisions(foes.get(i));
		}
		
		if (rupeesLeft() <= 0) {
			if (!gameOver) {
				gameOver = true;
				lastStepsLeft = LAST_STEPS;
			}
			
			if (gameOver) {
				if (lastStepsLeft <= 0) {
//					delegate.showEndScreen(true, score, timeLeft, bro.getHealth(), level);
					SceneDirector.getInstance().pushScene(new EndScreen(SceneDirector.getInstance().getContainer(), this, true));
//					SceneDirector.getInstance().popScene();

					return;
				} else {
					lastStepsLeft--;
				}
			}
		}
		
		if (timeLeft <= 0 || bro.getHealth() <= 0) {
			if (!gameOver) {
				gameOver = true;
				lastStepsLeft = LAST_STEPS;
			}
			
			if (gameOver) {
				if (lastStepsLeft <= 0) {
					SceneDirector.getInstance().pushScene(new EndScreen(SceneDirector.getInstance().getContainer(), this, false));

//					delegate.showEndScreen(false, score, 0, bro.getHealth(), level);
//					SceneDirector.getInstance().popScene();

					return;
				} else {
					lastStepsLeft--;
				}
			}
		}
	}
	
	public void cheat() {
		SceneDirector.getInstance().pushScene(new EndScreen(SceneDirector.getInstance().getContainer(), this, true));
	}
	
	public void resume() {
		super.resume();
		
		timeLeft = TIME_TO_WIN;
		level++;
		gameOver = false;
		
		foes.clear();
		stars.clear();
		hearts.clear();
		fires.clear();
		rupees.clear();
		bomb = null;
		
		randomlyFill();
		
		update();
	}
	
	/**
	 * Returns the number of rupees left to collect.
	 * 
	 * @return The number of rupees left.
	 */
	public int rupeesLeft() {
		int count = 0;
		
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				if (tiles[r][c].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[r][c];
					
					if (aBrick.getPrize().getClass() == Rupee.class) {
						count++;
					}
				}
			}
		}
		
		return count + rupees.size();
	}
	
	/**
	 * Returns whether a MovingSprite can move to a point without intersecting a wall
	 * 
	 * @param aPoint The point
	 * @param aSprite The moving sprite
	 * @return Whether a MovingSprite can move to a point without intersecting a wall
	 */
	public boolean canMoveToPoint(Point aPoint, MovingSprite sprite) {
		Rectangle spriteRect = new Rectangle(aPoint.x, aPoint.y, sprite.getSize().width, sprite.getSize().height);
		
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				if (tiles[r][c].isImpassable()) {
					Rectangle tileRect = new Rectangle(tiles[r][c].getLoc().x, tiles[r][c].getLoc().y, tiles[r][c].getSize().width, tiles[r][c].getSize().height);
					if (spriteRect.intersects(tileRect)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the successors of a foe at a point.
	 * 
	 * @param State
	 * @param aSprite The moving sprite
	 * @return Array of points and the directions to get there.
	 */
	public Iterator<State> getSuccessors(State state, MovingSprite sprite) {
		ArrayList<State> successors = new ArrayList<State>();
		Tile t = state.getTile();
		Point loc = t.getLoc();
		
		for (int i = 0; i < SpriteDirection.values().length; i++) {
			sprite.move(SpriteDirection.values()[i]);
			
			Point newPoint = new Point(loc.x + sprite.distanceToMove.x * sprite.getSize().width, loc.y + sprite.distanceToMove.y * sprite.getSize().height);
			if (canMoveToPoint(newPoint, sprite)) {
				if (tileForPoint(newPoint) != null) {
					successors.add(new State(tileForPoint(newPoint), SpriteDirection.values()[i], 1));
				}
			}
		}
		return successors.iterator();
	}

	/**
	 * Gets the tile that contains a point.
	 * 
	 * @param aPoint The point
	 * @return The tile the point is in
	 */
	public Tile tileForPoint(Point aPoint) {
		for (int r = 0; r < tiles.length-1; r++) {
			for (int c = 0; c < tiles[r].length-1; c++) {
				Tile aTile = tiles[r][c];
				Rectangle tileRect = new Rectangle(aTile.getLoc().x, aTile.getLoc().y, aTile.getSize().width, aTile.getSize().height);
				if (tileRect.contains(aPoint)) {
					return tiles[r][c];
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns whether a foe should change direction.
	 * 
	 * @param aFoe The moving foe 
	 * @return Whether a foe should change direction
	 */
	public boolean shouldChangeDirection(Foe aFoe) {
		Point foeCenter = aFoe.getCenter();
		Tile aTile = tileForPoint(foeCenter);
		if (aTile.getCenter().equals(foeCenter)) {
			return adjacentTileMoveExists(aTile);
		}
		
		return false;
	}
	
	/**
	 * Returns whether there is a tile horizontally or vertically adjacent
	 * to it that is passable.
	 * 
	 * @param aTile The center tile
	 * @return Whether an adjacent tile is passable
	 */
	public boolean adjacentTileMoveExists(Tile aTile) {
		int tileRow = aTile.row+1;
		int tileCol = aTile.col+1;
		
		// now check to make sure there is an alternative direction
		for (int r = Math.max(0, tileRow-1); r <= Math.min(tiles.length-1, tileRow+1); r++) {
			if (tiles[r][tileCol] != aTile && !tiles[r][tileCol].isImpassable()) {
				return true;
			}
		}
		
		for (int c = Math.max(0, tileCol-1); c <= Math.min(tiles[tiles.length-1].length, tileCol+1); c++) {
			if (tiles[tileRow][c] != aTile && !tiles[tileRow][c].isImpassable()) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Returns the corrected point for the MovingSprite to move to, based off of
	 * the point it wants to move to after it tries to move.
	 * 
	 * @param aPoint The point the MovingSprite wants to move to
	 * @param sprite The MovingSprite that is moving
	 * @return The corrected point
	 */
	public Point autoCorrectedPoint(Point aPoint, MovingSprite sprite) {
		Point oldPoint = sprite.getLoc();
		Rectangle spriteRect = new Rectangle(aPoint.x, aPoint.y, sprite.getSize().width, sprite.getSize().height);
		Point correctedPoint = aPoint;
		Wall wall = null;
		
		outer :
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				if (tiles[r][c].isImpassable()) {
					Rectangle tileRect = new Rectangle(tiles[r][c].getLoc().x, tiles[r][c].getLoc().y, tiles[r][c].getSize().width, tiles[r][c].getSize().height);
					if (spriteRect.intersects(tileRect)) {
						wall = (Wall) tiles[r][c];
						break outer;
					}
				}
			}
		}
		
		if (wall == null) {
			return aPoint;
		}
		
		Rectangle wallRect = new Rectangle(wall.getLoc().x, wall.getLoc().y, wall.getSize().width, wall.getSize().height); 
		
		Rectangle intersectRect = spriteRect.intersection(wallRect);
		
		final int maxIntersect = 15;
		
		if (correctedPoint.x != oldPoint.x) {
			if (intersectRect.height > maxIntersect) {
				return oldPoint;
			}
			
			int sign = 1;
			if (spriteRect.y < wallRect.y) {
				sign *= -1;
			}
			
			correctedPoint = new Point(correctedPoint.x, correctedPoint.y + sign * intersectRect.height);
		}
		
		else if(correctedPoint.y != oldPoint.y) {
			if (intersectRect.width > maxIntersect) {
				return oldPoint;
			}
			
			int sign = 1;
			if (spriteRect.x < wallRect.x) {
				sign *= -1;
			}
						
			correctedPoint = new Point(correctedPoint.x + sign * intersectRect.width, correctedPoint.y);
		}
		
		return correctedPoint;
	}
	
	/**
	 * Places a bomb at the tile the bro is at and plays a sonud effect.
	 */
	public void placeBomb() {
		if (bomb == null) {
			bomb = new Bomb(this);
			bomb.setLoc(tileForPoint(bro.getCenter()).getLoc());
			playSound("fuse.wav");
		}
	}

	/**
	 * Places fires at tile where bomb exploded and tiles within a horizontal/vertical range of the
	 * explosion. The fires stop when they hit an impassable wall or go outside the range. Plays a
	 * sound effect.
	 */
	public void didExplode() {
		playSound("explode.wav");
		
		Tile tile = tileForPoint(bomb.getCenter()); 
				
		// up
		for (int r = tile.row+1; r >= 0 && r >= tile.row+1 - MAX_BOMB_DISTANCE + 1; r--) {
			if (tiles[r][tile.col+1].isDestructible()) {
				if (tiles[r][tile.col+1].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[r][tile.col+1];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[r][tile.col+1] = new Grass(r, tile.col+1);
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.col+1]).getLoc());
				fires.add(aCloud);
				
				break;
			} else if (tiles[r][tile.col+1].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.col+1]).getLoc());
				fires.add(aCloud);
			}
		}
		
		// down
		for (int r = tile.row+1; r < tiles.length && r < tile.row+1 + MAX_BOMB_DISTANCE; r++) {
			if (tiles[r][tile.col+1].isDestructible()) {
				if (tiles[r][tile.col+1].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[r][tile.col+1];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[r][tile.col+1] = new Grass(r, tile.col+1);
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.col+1]).getLoc());
				fires.add(aCloud);
				
				break;
			} else if (tiles[r][tile.col+1].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.col+1]).getLoc());
				fires.add(aCloud);
			}
		}
		
		// left
		for (int c = tile.col+1; c >= 0 && c >= tile.col+1 - MAX_BOMB_DISTANCE + 1; c--) {
			if (tiles[tile.row+1][c].isDestructible()) {
				if (tiles[tile.row+1][c].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[tile.row+1][c];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[tile.row+1][c] = new Grass(tile.row+1, c);
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.row+1][c]).getLoc());
				fires.add(aCloud);
				
				break;
			} else if (tiles[tile.row+1][c].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.row+1][c]).getLoc());
				fires.add(aCloud);
			}
		}
		
		// right
		for (int c = tile.col+1; c < tiles[tile.row+1].length && c < tile.col+1 + MAX_BOMB_DISTANCE; c++) {
			if (tiles[tile.row+1][c].isDestructible()) {
				if (tiles[tile.row+1][c].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[tile.row+1][c];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[tile.row+1][c] = new Grass(tile.row+1, c);
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.row+1][c]).getLoc());
				fires.add(aCloud);

				break;
			} else if (tiles[tile.row+1][c].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.row+1][c]).getLoc());
				fires.add(aCloud);
			}
		}
		
		bomb = null;
	}

	/**
	 * Plays a WAV sound.
	 * 
	 * @param filename The file name of the WAV sound
	 */
	public void playSound(final String filename) {
        try {
			Clip sfx = AudioSystem.getClip();
			soundEffects.add(sfx);
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream("sounds/" + filename));
			sfx.open(inputStream);
			sfx.start(); 
        } catch (Exception e) {
        	System.err.println(e.getMessage());
        }
	}

	/**
	 * Removes the fire.
	 * 
	 * @param aFire The fire
	 */
	public void stopFire(Fire aFire) {
		fires.remove(aFire);
	}
	
	/**
	 * Checks to see if a sprite intersects with any other sprites. Performs an action based off of the
	 * type of sprite the tested sprite intersects with. 
	 * 
	 * @param sprite The sprite being tested
	 */
	public void checkCollisions(MovingSprite sprite) {
		Rectangle spriteRect = new Rectangle(sprite.getLoc().x, sprite.getLoc().y, sprite.getSize().width, sprite.getSize().height);

		for (int i = fires.size()-1; i >= 0; i--) {
			Fire aFire = fires.get(i);
			Rectangle fireRect = new Rectangle(aFire.getLoc().x, aFire.getLoc().y, aFire.getSize().width, aFire.getSize().height);
			
			if (fireRect.intersects(spriteRect)) {
				hurt(sprite);
				return;
			}
		}
		
		if (sprite == bro) {
			for (int i = rupees.size()-1; i >= 0; i--) {
				Rupee aRupee = rupees.get(i);
				Rectangle rupeeRect = new Rectangle(aRupee.getLoc().x, aRupee.getLoc().y, aRupee.getSize().width, aRupee.getSize().height);
				
				if (rupeeRect.intersects(spriteRect)) {
					collectRupee(aRupee);
				}
			}
			
			for (int i = hearts.size()-1; i >= 0; i--) {
				Heart aHeart = hearts.get(i);
				Rectangle heartRect = new Rectangle(aHeart.getLoc().x, aHeart.getLoc().y, aHeart.getSize().width, aHeart.getSize().height);
				
				if (heartRect.intersects(spriteRect)) {
					collectHeart(aHeart);
				}
			}
			
			for (int i = stars.size()-1; i >= 0; i--) {
				Star aStar = stars.get(i);
				Rectangle starRect = new Rectangle(aStar.getLoc().x, aStar.getLoc().y, aStar.getSize().width, aStar.getSize().height);
				
				if (starRect.intersects(spriteRect)) {
					collectStar(aStar);
				}
			}
			
			for (int i = foes.size()-1; i >= 0; i--) {
				Foe aFoe = foes.get(i);
				Rectangle foeRect = new Rectangle(aFoe.getLoc().x, aFoe.getLoc().y, aFoe.getSize().width, aFoe.getSize().height);
				
				if (foeRect.intersects(spriteRect)) {
					hurt(sprite);
					aFoe.setPauseTime(SceneDirector.getInstance().secondsToCycles(1));
					return;
				}
			}
		}
	}
	
	/**
	 * Hurts the moving sprite and plays a sound effect.
	 * 
	 * @param sprite The sprite to hurt
	 */
	public void hurt(MovingSprite sprite) {
		if (sprite.getImmunity() > 0) {
			return;
		}
		
		sprite.hurt();
		
		if (sprite == bro) {
			playSound("hurt.wav");
			addToScore(HURT_PUNISHMENT_SCORE_VALUE);
		}
		
		if (sprite.getHealth() == 0) {
			if (foes.contains(sprite)) {
				foes.remove(sprite);
				score += HURT_FOE_PUNISHMENT_SCORE_VALUE;
				addToScore(HURT_PUNISHMENT_SCORE_VALUE);
			}
		}
	}
	
	/**
	 * Adds to score the rupee's value and removes the rupee from the board.
	 * Plays a sound effect.
	 * 
	 * @param aRupee The rupee to collect
	 */
	public void collectRupee(Rupee aRupee) {
		addToScore(aRupee.getValue());
		rupees.remove(aRupee);
		
		playSound("rupee-colected.wav");
	}

	/**
	 * Adds to health the heart's value and removes the heart from the board.
	 * Plays a sound effect.
	 * 
	 * @param aHeart The heart to collect
	 */
	public void collectHeart(Heart aHeart) {
		addToHealth(aHeart.getValue());
		hearts.remove(aHeart);

		playSound("heart.wav");
	}
	
	/**
	 * Speeds up the bro and removes the star from the board.
	 * Plays a sound effect.
	 * 
	 * @param aStar The star to collect
	 */
	public void collectStar(Star aStar) {
		if (!bro.isSpedUp()) {
			bro.boostSpeed();
			stars.remove(aStar);
			
			playSound("speed-up.wav");
		}
	}
	
	/**
	 * Adds to score.
	 * 
	 * @param more Score to add
	 */
	public void addToScore(int more) {
		score = score + more;
	}
	
	/**
	 * Adds to health.
	 * 
	 * @param more Health to add
	 */
	public void addToHealth(int more) {
		bro.heal(more);
	}
	
	/**
	 * Places a sprite at the location of a tile.
	 * 
	 * @param aSprite The prize sprite to add
	 * @param aTile The tile to locate the sprite
	 */
	public void placePrizeOnTile(Sprite aSprite, Tile aTile) {
		aSprite.setLoc(aTile.getLoc());
		
		if (aSprite.getClass() == Rupee.class) {
			rupees.add((Rupee) aSprite);
		} else if (aSprite.getClass() == Heart.class) {
			hearts.add((Heart) aSprite);
		} else if (aSprite.getClass() == Star.class) {
			stars.add((Star) aSprite);
		}
	}

	public int heuristicForTile(Tile t) {
		return 0;
	}
	
	public int distanceFromBro(Point point) {
		return (int) Math.ceil((Math.abs(point.x - bro.loc.x) / Tile.size.width + Math.abs(point.y - bro.loc.y)) / Tile.size.height);
	}

	public boolean canSeeBro(MovingSprite sprite) {
		// make a line from the sprite to the tile and if at any point on the line the block intersecting is impassable, return false
		for (int x = Math.min(sprite.getCenter().x, bro.getCenter().x); x <= Math.max(sprite.getCenter().x, bro.getCenter().x); x++) {
			for (int y = Math.min(sprite.getCenter().y, bro.getCenter().y); y <= Math.max(sprite.getCenter().y, bro.getCenter().y); y++) {
				if (tileForPoint(new Point(x, y)).isImpassable()) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean isGoalState(State currentState) {
		if (currentState == null) {
			System.err.println("FOO!");
		}
		return currentState.getTile().equals(tileForPoint(bro.getCenter()));
	}

	public int getScore() {
		return score;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public int getLevel() {
		return level;
	}

	public void setScore(int totalScore) {
		this.score = totalScore;
	}
}

/**
 * GameViewDelegate
 * Required methods for classes that implement this interface.
 * 
 * @author Ryan Ashcraft
 */
interface GameViewDelegate {
	/**
	 * Starts the MIDI sequencer.
	 */
	public void startSequencer();
	/**
	 * Mutes the MIDI sequencer.
	 * 
	 * @param toMute Whether to mute the MIDI sequencer
	 */
	public void mute(boolean toMute);
	/**
	 * Shows the end screen to display information about the level just played.
	 * 
	 * @param won Whether the user won or lost
	 * @param score Score of previous game
	 * @param timeLeft The time left form the last level played
	 * @param health Health remaining from previous game
	 * @param lastLevelPlayed Numerical value of level of last level played
	 */
	public void showEndScreen(boolean won, int score, int timeLeft, int health, int lastLevelPlayed);
}