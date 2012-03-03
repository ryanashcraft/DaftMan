package com.ashcraftmedia.daftman.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.sound.midi.Sequencer;

import com.ashcraftmedia.daftman.core.Container;
import com.ashcraftmedia.daftman.core.DaftMan;
import com.ashcraftmedia.daftman.core.SoundStore;
import com.ashcraftmedia.daftman.search.HeuristicDelegate;
import com.ashcraftmedia.daftman.search.State;
import com.ashcraftmedia.daftman.sprite.Bomb;
import com.ashcraftmedia.daftman.sprite.BombDelegate;
import com.ashcraftmedia.daftman.sprite.Bro;
import com.ashcraftmedia.daftman.sprite.Fire;
import com.ashcraftmedia.daftman.sprite.FireDelegate;
import com.ashcraftmedia.daftman.sprite.Foe;
import com.ashcraftmedia.daftman.sprite.Heart;
import com.ashcraftmedia.daftman.sprite.MovingSprite;
import com.ashcraftmedia.daftman.sprite.MovingSpriteDelegate;
import com.ashcraftmedia.daftman.sprite.Rupee;
import com.ashcraftmedia.daftman.sprite.Sprite;
import com.ashcraftmedia.daftman.sprite.SpriteDirection;
import com.ashcraftmedia.daftman.sprite.Star;
import com.ashcraftmedia.daftman.tile.Brick;
import com.ashcraftmedia.daftman.tile.Grass;
import com.ashcraftmedia.daftman.tile.Tile;
import com.ashcraftmedia.daftman.tile.Wall;

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

public class GameScene extends Scene implements MovingSpriteDelegate, BombDelegate, FireDelegate, HeuristicDelegate {
	private static final int DEFAULT_SCORE = 0;
	private static final int DEFAULT_LEVEL = 1;
	private static final int DEFAULT_HEALTH = 3;
	private static final int HURT_PUNISHMENT_SCORE_VALUE = 0;
	private static final int HURT_FOE_PUNISHMENT_SCORE_VALUE = 5;
	private static final int TIME_TO_WIN = 120;
	private static final int LAST_STEPS = 20;
	
	private static final int BASE_NUMBER_OF_RUPEES = 10;
	private static final int ADD_NUMBER_OF_RUPEES_PER_LEVEL = 2;
	private static final int MAX_NUMBER_OF_RUPEES = 30;
	private static final int NUMBER_OF_HEARTS = 3;
	private static final int NUMBER_OF_STARS = 2;
	
	private static final int BASE_NUMBER_OF_FOES = 2;
	private static final int ADD_NUMBER_OF_FOES_PER_LEVEL = 1;
	private static final int BASE_FOE_PERSISTENCE = SceneDirector.get().secondsToCycles(2);
	private static final int ADD_FOE_PERSISTENCE = SceneDirector.get().secondsToCycles(0.5f);

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
	private final int OFFSET_X = -16;
	private final int OFFSET_Y = 0;

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
	public GameScene(Container container) {
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
	public GameScene(Container container, String[] stringArray) {
		super(container);
		
		score = DEFAULT_SCORE;
		level = DEFAULT_LEVEL;
		
		scoreBoard = new ScoreBoard(new Point(-OFFSET_X, - SCOREBOARD_HEIGHT - OFFSET_Y), new Dimension(container.getDimension().width + OFFSET_X, SCOREBOARD_HEIGHT));
				
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
		
		int foesToAdd = BASE_NUMBER_OF_FOES + (level - 1) * ADD_NUMBER_OF_FOES_PER_LEVEL;
		if (DaftMan.DEBUG) {
			foesToAdd = 0;
		}
		for (int i = 0; i < foesToAdd; i++) {
			Foe aFoe = new Foe(this, this);
			Tile aTile = null;

			// if can't find suitable place for foe, don't run into an infinite loop
			final int MAX_ATTEMPTS = 50;
			int attempts = 0;
			while((attempts <= MAX_ATTEMPTS) && (aTile == null || aTile.isImpassable() || !adjacentTileMoveExists(aTile) || (aTile.getRow() < 5 && aTile.getCol() < 5))) {
				aTile = tiles[ranGen.nextInt(tiles.length-1)][ranGen.nextInt(tiles[tiles.length-1].length-1)];
				attempts++;
			}
			aFoe.setLoc(aTile.getLoc());
			
			aFoe.setMaxPersistence(BASE_FOE_PERSISTENCE + (level - 1) * ADD_FOE_PERSISTENCE);
			
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
		
		g.translate(OFFSET_X, SCOREBOARD_HEIGHT + OFFSET_Y);
		
		scoreBoard.draw(g);
		
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				tiles[r][c].draw(g);
				
				if (DaftMan.DEBUG && !isTileSafe(tiles[r][c])) {
					g.setColor(Color.pink);
					g.fillRect(tiles[r][c].getCol() * Tile.size.width, tiles[r][c].getRow() * Tile.size.height, Tile.size.width, Tile.size.height);
				}
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
			case KeyEvent.VK_C: if (DaftMan.DEBUG) { cheat(); } break;
			case KeyEvent.VK_M: SoundStore.get().mute(); break;
			case KeyEvent.VK_Q: SceneDirector.get().popToRootScene(); break;
			case KeyEvent.VK_P: SceneDirector.get().pushScene(new PauseScene(SceneDirector.get().getContainer())); break;
			case KeyEvent.VK_D:
				if (e.isShiftDown()) {
					DaftMan.DEBUG = !DaftMan.DEBUG;
				}
				break;
		}
	}

	/**
	 * Called when user releases a key. Performs specified action based on key released.
	 * 
	 * @param e The KeyEvent object
	 */
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
			bro.stopMoveY();
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			bro.stopMoveX();
		}
	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		
		if (DaftMan.DEBUG) {
			Foe newFoe = new Foe(this, this);
			newFoe.setLoc(tileForPoint(new Point(e.getPoint().x - OFFSET_X, e.getPoint().y - SCOREBOARD_HEIGHT - OFFSET_Y)).getLoc());
			newFoe.setMaxPersistence(BASE_FOE_PERSISTENCE + (level - 1) * ADD_FOE_PERSISTENCE);
			foes.add(newFoe);
		}
	}
	
	/**
	 * Called when timer fires and tells each sprite to do something. Calls to check
	 * collisions on all moving sprites (bro and foes), and checks to see if game should
	 * end. Also sets the scoreboard's values.
	 */
	public void update() {
		super.update();
		
		if (getCycleCount() <= 1) {
			SoundStore.get().playSound("DA_FUNK", Sequencer.LOOP_CONTINUOUSLY, 120.0f, false);
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
		
		if (!DaftMan.DEBUG && getCycleCount() != 0 && getCycleCount() % SceneDirector.get().secondsToCycles(1) == 0) {
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
					SceneDirector.get().pushScene(new EndScene(SceneDirector.get().getContainer(), this, true));

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
					SceneDirector.get().pushScene(new EndScene(SceneDirector.get().getContainer(), this, false));

					return;
				} else {
					lastStepsLeft--;
				}
			}
		}
	}
	
	public void cheat() {
		SceneDirector.get().pushScene(new EndScene(SceneDirector.get().getContainer(), this, true));
	}
	
	public void resume(Scene lastScene) {
		super.resume(lastScene);
		
		if (lastScene != null && lastScene instanceof EndScene) {
			timeLeft = TIME_TO_WIN;
			level++;
			gameOver = false;
			
			foes.clear();
			stars.clear();
			hearts.clear();
			fires.clear();
			rupees.clear();
			bomb = null;
			
			bro.setImmunity(0);
			bro.resetSpeed();
			bro.setDirection(SpriteDirection.STOP);
			
			randomlyFill();
			
			update();
		}
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
	
	/*
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
	
	public static double euclidieanDistance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	/**
	 * Gets the tile that contains a point.
	 * 
	 * @param aPoint The point
	 * @return The tile the point is in
	 */
	public Tile tileForPoint(Point aPoint) {
		int row = aPoint.y / Tile.size.height;
		int col = aPoint.x / Tile.size.width;
		
		if (row < tiles.length - 1 && col < tiles[row].length - 1) {
			return tiles[row][col];
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
		
		if (!isTileSafe(aTile)) {
			return true;
		}
		
		Tile tileHeadedFor = tileHeadedFor(aFoe);
		if (tileHeadedFor != null && !isTileSafe(tileHeadedFor(aFoe))) {
			return true;
		}
		
		if (aTile.getCenter().equals(foeCenter)) {
			return adjacentTileMoveExists(aTile);
		}
		
		return false;
	}
	
	private Tile tileHeadedFor(MovingSprite sprite) {
		Tile currentTile = tileForPoint(sprite.getCenter());
		switch (sprite.getDirection()) {
			case UP:
				return tiles[currentTile.getRow() - 1][currentTile.getCol()];
			case RIGHT:
				return tiles[currentTile.getRow()][currentTile.getCol() + 1];
			case DOWN:
				return tiles[currentTile.getRow() + 1][currentTile.getCol()];
			case LEFT:
				return tiles[currentTile.getRow()][currentTile.getCol() - 1];
		}
		
		return null;
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
			SoundStore.get().playSound("FUSE");
		}
	}

	/**
	 * Places fires at tile where bomb exploded and tiles within a horizontal/vertical range of the
	 * explosion. The fires stop when they hit an impassable wall or go outside the range. Plays a
	 * sound effect.
	 */
	public void didExplode() {
		SoundStore.get().playSound("EXPLODE");
		
		Tile tile = tileForPoint(bomb.getCenter()); 
				
		// up
		for (int r = tile.getRow(); r >= 0 && r >= tile.getRow() - MAX_BOMB_DISTANCE + 1; r--) {
			if (tiles[r][tile.getCol()].isDestructible()) {
				if (tiles[r][tile.getCol()].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[r][tile.getCol()];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[r][tile.getCol()] = new Grass(r, tile.getCol());
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.getCol()]).getLoc());
				fires.add(aCloud);
				
				break;
			} else if (tiles[r][tile.getCol()].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.getCol()]).getLoc());
				fires.add(aCloud);
			}
		}
		
		// down
		for (int r = tile.getRow(); r < tiles.length && r < tile.getRow() + MAX_BOMB_DISTANCE; r++) {
			if (tiles[r][tile.getCol()].isDestructible()) {
				if (tiles[r][tile.getCol()].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[r][tile.getCol()];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[r][tile.getCol()] = new Grass(r, tile.getCol());
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.getCol()]).getLoc());
				fires.add(aCloud);
				
				break;
			} else if (tiles[r][tile.getCol()].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[r][tile.getCol()]).getLoc());
				fires.add(aCloud);
			}
		}
		
		// left
		for (int c = tile.getCol(); c >= 0 && c >= tile.getCol() - MAX_BOMB_DISTANCE + 1; c--) {
			if (tiles[tile.getRow()][c].isDestructible()) {
				if (tiles[tile.getRow()][c].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[tile.getRow()][c];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[tile.getRow()][c] = new Grass(tile.getRow(), c);
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.getRow()][c]).getLoc());
				fires.add(aCloud);
				
				break;
			} else if (tiles[tile.getRow()][c].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.getRow()][c]).getLoc());
				fires.add(aCloud);
			}
		}
		
		// right
		for (int c = tile.getCol(); c < tiles[tile.getRow()].length && c < tile.getCol() + MAX_BOMB_DISTANCE; c++) {
			if (tiles[tile.getRow()][c].isDestructible()) {
				if (tiles[tile.getRow()][c].getClass() == Brick.class) {
					Brick aBrick = (Brick) tiles[tile.getRow()][c];
					placePrizeOnTile(aBrick.getPrize(), aBrick);
				}
				
				tiles[tile.getRow()][c] = new Grass(tile.getRow(), c);
								
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.getRow()][c]).getLoc());
				fires.add(aCloud);

				break;
			} else if (tiles[tile.getRow()][c].isImpassable()) {
				break;
			} else {
				Fire aCloud = new Fire(this);
				aCloud.setLoc((tiles[tile.getRow()][c]).getLoc());
				fires.add(aCloud);
			}
		}
		
		bomb = null;
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
					aFoe.setPauseTime(SceneDirector.get().secondsToCycles(1));
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
			SoundStore.get().playSound("HURT");
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
		
		SoundStore.get().playSound("RUPEE_COLLECTED");
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

		SoundStore.get().playSound("HEART");
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
			
			SoundStore.get().playSound("SPEED_UP");
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
		return distanceFromBro(t.getCenter());
	}
	
	public int distanceFromBro(Point point) {
		return (int) Math.ceil((Math.abs(point.x - bro.getLoc().x) / Tile.size.width + Math.abs(point.y - bro.getLoc().y)) / Tile.size.height);
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

	/**
	 * Returns the successors of a foe at a point.
	 * 
	 * @param State
	 * @param aSprite The moving sprite
	 * @return Array of points and the directions to get there.
	 */
	public List<State> getSuccessors(State state, MovingSprite sprite) {
		ArrayList<State> successors = new ArrayList<State>();
		Tile t = state.getTile();
		Point loc = t.getLoc();
		
		for (int i = 0; i < SpriteDirection.values().length; i++) {
			sprite.move(SpriteDirection.values()[i]);
			
			Point newPoint = new Point(loc.x + sprite.getDistanceToMove().x * sprite.getSize().width, loc.y + sprite.getDistanceToMove().y * sprite.getSize().height);
			if (canMoveToPoint(newPoint, sprite)) {
				Tile tile = tileForPoint(newPoint);
				if (tile != null) {
					int cost = 1;
					
					if (!isTileSafe(tile)) {
						cost = Integer.MAX_VALUE;
					}
					
					successors.add(new State(tile, SpriteDirection.values()[i], cost));
				}
			}
		}
		
		return successors;
	}

	public boolean isGoalState(State currentState) {
		if (bomb != null) {
			Tile broTile = tileForPoint(bro.getCenter());
			if (!isTileSafe(broTile)) {
				Iterator<Tile> adjacentTiles = adjacentTiles(currentState.getTile());
				while (adjacentTiles.hasNext()) {
					if (!isTileSafe(adjacentTiles.next())) {
						return true;
					}
				}
			}
		}
		
		return currentState.getTile().equals(tileForPoint(bro.getCenter()));
	}
	
	/**
	 * Returns whether there is a tile horizontally or vertically adjacent
	 * to it that is passable.
	 * 
	 * @param aTile The center tile
	 * @return Whether an adjacent tile is passable
	 */
	public boolean adjacentTileMoveExists(Tile aTile) {
		int tileRow = aTile.getRow();
		int tileCol = aTile.getCol();
		
		// now check to make sure there is an alternative direction
		for (int r = Math.max(0, tileRow-1); r <= Math.min(tiles.length-1, tileRow+1); r++) {
			if (tiles[r][tileCol] != aTile && !tiles[r][tileCol].isImpassable() && isTileSafe(tiles[r][tileCol])) {
				return true;
			}
		}
		
		for (int c = Math.max(0, tileCol-1); c <= Math.min(tiles[tiles.length-1].length, tileCol+1); c++) {
			if (tiles[tileRow][c] != aTile && !tiles[tileRow][c].isImpassable() && isTileSafe(tiles[tileRow][c])) {
				return true;
			}
		}
		
		return false;
	}

	public Iterator<Tile> adjacentTiles(Tile aTile) {
		int tileRow = aTile.getRow();
		int tileCol = aTile.getCol();
		
		ArrayList<Tile> adjacentTiles = new ArrayList<Tile>();
		
		// now check to make sure there is an alternative direction
		for (int r = Math.max(0, tileRow-1); r <= Math.min(tiles.length-1, tileRow+1); r++) {
			if (tiles[r][tileCol] != aTile && !tiles[r][tileCol].isImpassable()) {
				adjacentTiles.add(tiles[r][tileCol]);
			}
		}
		
		for (int c = Math.max(0, tileCol-1); c <= Math.min(tiles[tiles.length-1].length, tileCol+1); c++) {
			if (tiles[tileRow][c] != aTile && !tiles[tileRow][c].isImpassable()) {
				adjacentTiles.add(tiles[tileRow][c]);
			}
		}
		
		return adjacentTiles.iterator();
	}
	
	public double distanceToTileDistance(double distance) {
		return distance / ((Tile.size.height + Tile.size.width) / 2); 
	}
	
	public boolean isTileSafe(Tile tile) {
		if (tile.isImpassable()) {
			return true;
		}
		
		for (Fire fire : fires) {
			if (tileForPoint(fire.getCenter()) == tile) {
				return false;
			}
		}
				
		if (bomb != null) {
			// in same column
			if (tile.getCenter().x == bomb.getCenter().x) {
				if (Math.abs(tile.getRow() - tileForPoint(bomb.getCenter()).getRow()) < MAX_BOMB_DISTANCE) {
					int bombRow = tileForPoint(bomb.getCenter()).getRow();
					if (tile.getRow() < bombRow) {
						for (int r = bombRow; r > tile.getRow(); --r) {
							if (tiles[r][tile.getCol()].isImpassable()) {
								return true;
							}
						}
					} else if (tile.getRow() > bombRow) {
						for (int r = bombRow; r <= tile.getRow(); r++) {
							if (tiles[r][tile.getCol()].isImpassable()) {
								return true;
							}
						}
					}
					
					return false;
				}
			} else if (tile.getCenter().y == bomb.getCenter().y) {
				if (Math.abs(tile.getCol() - tileForPoint(bomb.getCenter()).getCol()) < MAX_BOMB_DISTANCE) {
					int bombCol = tileForPoint(bomb.getCenter()).getCol();
					if (tile.getCol() < bombCol) {
						for (int c = bombCol; c > tile.getCol(); --c) {
							if (tiles[tile.getRow()][c].isImpassable()) {
								return true;
							}
						}
					} else if (tile.getCol() > bombCol) {
						for (int c = bombCol; c <= tile.getCol(); c++) {
							if (tiles[tile.getRow()][c].isImpassable()) {
								return true;
							}
						}
					}
					
					return false;
				}
			}
		}
		
		return true;
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