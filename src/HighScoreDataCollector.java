import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public final class HighScoreDataCollector {
	private static HighScoreDataCollector instance;
	private static final int HIGH_SCORE_RECORD_COUNT = 10;
	
	private int[] recordScores = new int[HIGH_SCORE_RECORD_COUNT];
	private String[] recordHolders = new String[HIGH_SCORE_RECORD_COUNT];
	private static Connection connection;
	
	public static HighScoreDataCollector getInstance() {
		if (instance == null) {
			instance = new HighScoreDataCollector();
		}
		
		return instance;
	}
	
	private HighScoreDataCollector() {
		
	}
	
	public int[] getRecordScores() {
		return recordScores;
	}
	
	public String[] getRecordHolders() {
		return recordHolders;
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
	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
