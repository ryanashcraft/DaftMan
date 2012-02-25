
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import core.ImageStore;



/**
 * HW10: DAFTMAN
 * This application is a take on the classic video game "Bomberman" by Hudson Soft,
 * with some design and theme inspirations from Daft Punk. Some homages to other games are included,
 * including The Legend of Zelda, Super Mario Bros, Super Mario Kart, Sonic, and Minecraft.
 * 
 * You play by trying to destroy the bricks and collecting all the rupees in each level and avoiding
 * monsters and being hit by a bomb's explosion.
 * 
 * Extra features include: 
 *  - Fancy graphics
 *  - Infinite levels with increased difficulty
 *  - Time limit per limit
 *  - Pause/Resume in-game
 *  - Health
 *  - Stars that increase speed, Sonic/Mario Kart style
 * 	- MIDI background music sound clips
 *  - WAV sound effects
 *  - Read text files for creating board (test purposes only, not available when running in browser due to Java security permissions)
 *  - Read/write to high scores database in MySQL server
 *  
 * I worked on this assignment alone, using course materials, previous work,
 * the online Java API. I also used several websites that explained how to use
 * certain Java APIs. I sourced the website above each method or line where I used
 * code that I learned about from that website.
 *  
 * Game.java
 * This class manages the views, sounds, and high score list.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 12/03/2010
 */

public class Game extends JApplet implements MainMenuDelegate, GameViewDelegate, EndScreenDelegate, HighScoreViewDelegate {
	private final Dimension d = new Dimension(16 * 32, 100 + 12 * 32);
	private MainMenu mainMenu;
	private GameView gameView;
	private EndScreen endScreen;
	private HighScoreView highScoreView;
	
	private static Sequencer sequencer;
	
	private static Image wallImage;
	private static Image brickImage;
	
	private static Image[] broUpImages;
	private static Image[] broDownImages;
	private static Image[] broLeftImages;
	private static Image[] broRightImages;
	private static Image[] foeUpImages;
	private static Image[] foeDownImages;
	private static Image[] foeLeftImages;
	private static Image[] foeRightImages;
	
	private static Image[] bombImages;
	private static Image[] fireImages;
	private static Image yellowRupeeImage;
	private static Image blueRupeeImage;
	private static Image heartImage;
	public static Image smallHeartImage;
	public static Image starImage;
	
	public static Font h1;
	public static Font h2;
	public static Font font;
	
	private static Connection connection;
	
	private final int DEFAULT_SCORE = 0;
	private final int DEFAULT_LEVEL = 1;
	private final int DEFAULT_HEALTH = 3;
	
	private boolean muted;
	
	final int HIGH_SCORE_RECORD_COUNT = 10;
	int[] recordScores = new int[HIGH_SCORE_RECORD_COUNT];
	String[] recordHolders = new String[HIGH_SCORE_RECORD_COUNT];
		
	/**
	 * Initializes the applet.
	 */
	public void init() {
		setSize(d);
		
		prepareResources();
		
		showMainMenu();
	}
	
	/**
	 * Called when the applet is closed. Closes the mySQL connection, stops the timers
	 * and stops all sounds.
	 */
	public void destroy() {
		try {
			closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		stopSequencer();
		if (sequencer != null) {
			if (sequencer.isOpen()) {
				sequencer.close();
			}
		}
		
		if (mainMenu != null) {
			mainMenu.stop();
		}
		
		if (gameView != null) {
			gameView.stop();
		}
		
		mainMenu = null;
		highScoreView = null;
		gameView = null;
		endScreen = null;
		
		sequencer = null;
		connection = null;
	}
	
	/**
	 * Reads all images, sounds, and fonts used in program. Also calls to retrieve
	 * high score list.
	 */
	public void prepareResources() {
		// use a media tracker to make sure all images are fully loaded
		
		MediaTracker mt = new MediaTracker(this);
		int imagesCount = 0;
		
		bombImages = new Image[2];
		for (int i = 0; i < bombImages.length; i++) {
			bombImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/bomb-" + (i+1) + ".png"));
			mt.addImage(bombImages[i], imagesCount++);
		}
		
		fireImages = new Image[2];
		for (int i = 0; i < fireImages.length; i++) {
			fireImages[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/fire-" + (i+1) + ".png"));
			mt.addImage(fireImages[i], imagesCount++);
		}
		
		try {
        	mt.waitForAll();
        } catch (InterruptedException e) {
            return;
        }
        
        Bomb.bombImages = bombImages;
        Fire.fireImages = fireImages;

        smallHeartImage = ImageStore.get().getImage("SMALL_HEART");
        
        font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("fonts/ARCADECLASSIC.TTF"));
		} catch(Exception e){
			font = new Font("Sans-Serif", Font.PLAIN, 26);
			e.printStackTrace();
		}
		
		font = font.deriveFont(Font.PLAIN, 26);
		h1 = font.deriveFont(Font.PLAIN, 72);
		h2 = font.deriveFont(Font.PLAIN, 48);
		
		getHighScoreRecords();
	}
	
	/**
	 * Returns the default size.
	 * 
	 * @return The default size
	 */
	public Dimension getDefaultSize() {
		return getSize();
	}
	
	private void forceRequestFocus() {
		Timer focusTimer = new Timer(5, new RequestFocusActionListener());
		focusTimer.setRepeats(false);
		focusTimer.start();
	}
	
	private class RequestFocusActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			mainMenu.requestFocusInWindow();
		}
	}
	
	/**
	 * Close all other views and show main menu view.
	 */
	public void showMainMenu() {
		if (mainMenu == null) {
			setSequence("dafunk.mid", 0, 120.0f, false);
			mainMenu = new MainMenu(d, this);
		}

		getContentPane().add(mainMenu);
		
		if (endScreen != null) {
			endScreen.stop();
			endScreen.transferFocus();
			getContentPane().remove(endScreen);
			endScreen = null;
		} else if (highScoreView != null) {
			highScoreView.transferFocus();
			getContentPane().remove(highScoreView);
			highScoreView = null;
		} else {
			forceRequestFocus();
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Show high score list view.
	 */
	public void showHighScoreView() {
		getHighScoreRecords();
		
		highScoreView = new HighScoreView(d, recordScores, recordHolders, this);
		getContentPane().add(highScoreView);
		
		if (mainMenu != null) {
			mainMenu.transferFocus();
			getContentPane().remove(mainMenu);
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Processes a SQL command to insert a score into the database.
	 * 
	 * @param score The score
	 * @param name The scorer's name
	 */
	public void recordScore(int score, String name) {	
		String sql = "INSERT INTO records (Score, Name) VALUES (" + score + ", '" + name + "');";
		try {
			updateSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes a SQL command to read the top scores from the database.
	 */
	private void getHighScoreRecords() {
		ResultSet rs = null;
		String sql = "SELECT name, score FROM records ORDER BY score DESC;";
		try {
			rs = selectSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (rs == null) {
			return;
		}
		
		recordScores = new int[HIGH_SCORE_RECORD_COUNT];
		recordHolders = new String[HIGH_SCORE_RECORD_COUNT];
		try {
			rs.beforeFirst();
			for (int i = 0; rs.next() && i < HIGH_SCORE_RECORD_COUNT; i++) {
				recordHolders[i] = rs.getString(1);
				recordScores[i] = Integer.parseInt(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Presents a file chooser and if a file is selected, reads it and
	 * converts into an String array of each line.
	 */
	public void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new TXTFilter());

		int returnVal = fileChooser.showOpenDialog(this);
				
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			try {
				Scanner scanner = new Scanner(file, "UTF-8");
				StringBuilder sb = new StringBuilder();
				while(scanner.hasNext()) {
					sb.append(scanner.next());
					sb.append(System.getProperty("line.separator"));
				}
				String readString = sb.toString();
				
				if (stringIsValid(readString)) {
					String[] stringArray = readString.split(System.getProperty("line.separator"));
					startGame(stringArray);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Checks to make sure string can be used in creating a board. Shows warning dialog
	 * if string is not valid.
	 * 
	 * @param string String from file read
	 * @return Validity
	 */
	private boolean stringIsValid(String string) {
		String[] lines = string.split(System.getProperty("line.separator"));

		for (int r = 0; r < lines.length; r++) {
			for (int c = 0; c < lines[r].length(); c++) {	
				if (lines[r].charAt(c) != ',' && lines[r].charAt(c) != '1' && lines[r].charAt(c) != '2' && lines[r].charAt(c) != 'w' && lines[r].charAt(c) != 'g' && lines[r].charAt(c) != 'h' && lines[r].charAt(c) != 'r' && lines[r].charAt(c) != 's') {
					JOptionPane.showMessageDialog(null, "Invalid File. Invalid characters found.", "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		}
		
		int broCount = 0;
		int rupeeCount = 0;
		for (int r = 0; r < lines.length; r++) {
			for (int c = 0; c < lines[r].length(); c++) {			
				if (lines[r].charAt(c) == '1') {
					broCount++;
				} else if (lines[r].charAt(c) == 'r') {
					rupeeCount++;
				}
			}
		}
		if (broCount != 1) {
			JOptionPane.showMessageDialog(null, "Invalid file. No first player was not defined.", "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		if (rupeeCount < 1) {
			JOptionPane.showMessageDialog(null, "Invalid file. No rupees defined.", "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		int firstLineLength = lines[0].length();
		for (int r = 0; r < lines.length; r++) {
			if (lines[r].length() != firstLineLength) {
				JOptionPane.showMessageDialog(null, "Invalid file. Column inconsistencies.", "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
				
		return sizeMeetsRequirements(lines.length, firstLineLength);
	}
	
	
	/**
	 * Checks to see if number of cols/rows meets the requirements. If not,
	 * shows a warning dialog.
	 * 
	 * @param rows Number of rows
	 * @param cols Number of columns
	 * @return If size met requirements
	 */
	public boolean sizeMeetsRequirements(int rows, int cols) {
		final int REQ_ROWS = 11;
		final int REQ_COLS = 15;
		
		if (rows != REQ_ROWS || cols != REQ_COLS) {
			JOptionPane.showMessageDialog(null, "Invalid file. Board size must be " +  REQ_ROWS + " x " + REQ_COLS + ".", "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		return true;
	}

	/**
	 * Closes other views and shows a game view with a soundtrack.
	 */
	public void startGame() {
		setSequence("aroundtheworld.mid", Sequencer.LOOP_CONTINUOUSLY, 120.0f, false);
		
		gameView = new GameView(d, DEFAULT_SCORE, DEFAULT_LEVEL, DEFAULT_HEALTH, this);
		gameView.setMuted(muted);
		getContentPane().add(gameView);
		
		if (mainMenu != null) {
			mainMenu.stop();
			mainMenu.transferFocus();
			getContentPane().remove(mainMenu);
			mainMenu = null;
		}
		
		if (highScoreView != null) {
			highScoreView.transferFocus();
			getContentPane().remove(highScoreView);
			highScoreView = null;
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Closes other views and shows a game view with a soundtrack. The game view
	 * is given an array of strings (each string is a row) to fill the board.
	 * 
	 * @param stringArray An array of strings, where each string is a row
	 */
	private void startGame(String[] stringArray) {
		setSequence("aroundtheworld.mid", Sequencer.LOOP_CONTINUOUSLY, 120.0f, false);
		
		gameView = new GameView(d, stringArray, DEFAULT_SCORE, DEFAULT_LEVEL, DEFAULT_HEALTH, this);
		getContentPane().add(gameView);
		
		if (mainMenu != null) {
			mainMenu.stop();
			mainMenu.transferFocus();
			getContentPane().remove(mainMenu);
			mainMenu = null;
		}

		if (highScoreView != null) {
			highScoreView.transferFocus();
			getContentPane().remove(highScoreView);
			highScoreView = null;
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Starts a new level by creating a new game view
	 * 
	 * @param score The score to start with
	 * @param level The numerical value of the new level
	 * @param health The health to start with
	 */
	public void newLevel(int score, int level, int health) {
		setSequence("aroundtheworld.mid", Sequencer.LOOP_CONTINUOUSLY, 120.0f, false);
		
		gameView = new GameView(d, score, level, health, this);
		gameView.setMuted(muted);
		getContentPane().add(gameView);
		
		if (endScreen != null) {
			endScreen.stop();
			endScreen.transferFocus();
			getContentPane().remove(endScreen);
			endScreen = null;
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Removes the game view and shows the end screen.
	 * 
	 * @param won Whether the user won or lost
	 * @param score Score of previous game
	 * @param health Health remaining from previous game
	 * @param lastLevelPlayed Numerical value of level of last level played
	 */
	public void showEndScreen(boolean won, int score, int timeLeft, int health, int lastLevelPlayed) {
		setSequence("stronger.mid", 0, 0.0f, false);
		
		endScreen = new EndScreen(d, won, score, timeLeft, health, lastLevelPlayed, this);
		getContentPane().add(endScreen);
		
		if (gameView != null) {
			gameView.stop();
			getContentPane().remove(gameView);
			gameView = null;
		}
		
		validate();
		repaint();
	}
	
	/**
	 * Sets the MIDI sequence to play
	 * 
	 * @param filename The file name of the MIDI sound file
	 * @param repeat Whether to repeat the MIDI file
	 * @param bpm The tempo in beats per minute for the clip 
	 * @param overlay Whether to stop previous track
	 */
	public void setSequence(String filename, int repeat, float bpm, boolean overlay) {
		if (!overlay) {
			if (sequencer != null) {
				if (sequencer.isRunning()) {
					sequencer.stop();
				}
			}
		}
		
		try {
		    Sequence sequence = MidiSystem.getSequence(getClass().getResource("sounds/" + filename));

		    sequencer = MidiSystem.getSequencer();
		    sequencer.setLoopCount(repeat);
		    if (bpm != 0.0) {
		    	sequencer.setTempoInBPM(bpm);
		    }
		    sequencer.open();
		    sequencer.setSequence(sequence);
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} catch (MidiUnavailableException e) {
		} catch (InvalidMidiDataException e) {
		}
	}
	
	/**
	 * Plays the active MIDI tracks.
	 */
	public void startSequencer() {
		if (muted) {
			return;
		}
		
		if (sequencer != null && sequencer.isOpen()) {
			if (!sequencer.isRunning()) {
				sequencer.start();
			}
		}
	}
	
	/**
	 * Stops the active MIDI tracks.
	 */
	public void stopSequencer() {
		if (sequencer != null && sequencer.isOpen()) {
			if (sequencer.isRunning()) {
				sequencer.stop();
			}
		}
	}
	
	/**
	 * Mute the active MIDI tracks.
	 * 
	 * @param toMute Whether to mute
	 */
	public void mute(boolean toMute) {
		muted = toMute;
		
		muteSounds();
	}
	
	/**
	 * Switches whether to mute the active MIDI tracks.
	 */
	public void mute() {
		muted = !muted;
		
		muteSounds();
	}
	
	/**
	 * Tells the sequencer to stop the track, which essentially
	 * mutes it.
	 */
	private void muteSounds() {
		if (muted) {
			if (sequencer != null && sequencer.isOpen()) {
				if (sequencer.isRunning()) {
					sequencer.stop();
				}
			}
		} else {
			if (sequencer != null && sequencer.isOpen()) {
				if (!sequencer.isRunning()) {
					sequencer.start();
					sequencer.setTempoInBPM(120.0f);
				}
			}
		}
	}
	
	/* -----------
	 * mySQL
	 * -----------
	 */
	/**
	 * Makes a connection if doesn't exist to the mySQL server and performs
	 * an update operation on the connection.
	 * 
	 * @param sql The SQL update command
	 * @return The result of the SQL udpate operation
	 * @throws SQLException A connection exception
	 */
	private int updateSQL(String sql) throws SQLException {
		if (connection == null || connection.isClosed()) {
			String userName = "";
	        String password = "";
	        String url = "";
	        try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			}
	        catch (InstantiationException e) { e.printStackTrace(); }
			catch (IllegalAccessException e) { e.printStackTrace(); }
			catch (ClassNotFoundException e) { e.printStackTrace(); }
	        
			try {
				connection = DriverManager.getConnection(url, userName, password);
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			    System.out.println("SQLState: " + e.getSQLState());
			    System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		
		if (connection == null) {
			return 0;
		}
				
		Statement statement = connection.createStatement();
		return statement.executeUpdate(sql);	
	}
	
	/**
	 * Makes a connection if doesn't exist to the mySQL server and performs
	 * a select/query operation on the connection.
	 * 
	 * @param sql The SQL select command
	 * @return The result set of the SQL query
	 * @throws SQLException A connection exception
	 */
	private ResultSet selectSQL(String sql) throws SQLException {
		if (connection == null || connection.isClosed()) {
			String userName = "";
	        String password = "";
	        String url = "";
	        try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			}
	        catch (InstantiationException e) { e.printStackTrace(); }
			catch (IllegalAccessException e) { e.printStackTrace(); }
			catch (ClassNotFoundException e) { e.printStackTrace(); }
	        
			try {
				connection = DriverManager.getConnection(url, userName, password);
			} catch (SQLException e) {
			    System.out.println("SQLException: " + e.getMessage());
			    System.out.println("SQLState: " + e.getSQLState());
			    System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		
		if (connection == null) {
			return null;
		}
		
		Statement statement = connection.createStatement();
		return statement.executeQuery(sql);
	}
	
	/**
	 * Closes the SQL connection, if exists.
	 * 
	 * @throws SQLException A connection exception
	 */
	private void closeConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
			connection = null;
		}
	}
	
	/**
	 * Adds extra spaces to a string, for appearances sake.
	 * 
	 * @param str The original string
	 * @return The string with extra spaces
	 */
	public static String addExtraSpaces(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') {
				sb.append("    ");
			} else {
				sb.append(str.charAt(i));
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Removes extra spaces from a string (reversing effects of
	 * public static String addExtraSpaces(String str) method.
	 * 
	 * @param str The original string
	 * @return The string without the extra spaces
	 */
	public static String removeExtraSpaces(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (i > 0 && str.charAt(i) == ' ' && str.charAt(i-1) == ' ') {
				continue;
			} else {
				sb.append(str.charAt(i));
			}
		}
		
		return sb.toString();
	}
}
