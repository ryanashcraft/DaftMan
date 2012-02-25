import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public final class LevelReader {
	/**
	 * Presents a file chooser and if a file is selected, reads it and
	 * converts into an String array of each line.
	 */
	public static String[] readLevelFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new TXTFilter());

		int returnVal = fileChooser.showOpenDialog(SceneDirector.getInstance());
				
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
					return stringArray;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Checks to make sure string can be used in creating a board. Shows warning dialog
	 * if string is not valid.
	 * 
	 * @param string String from file read
	 * @return Validity
	 */
	private static boolean stringIsValid(String string) {
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
	public static boolean sizeMeetsRequirements(int rows, int cols) {
		final int REQ_ROWS = 11;
		final int REQ_COLS = 15;
		
		if (rows != REQ_ROWS || cols != REQ_COLS) {
			JOptionPane.showMessageDialog(null, "Invalid file. Board size must be " +  REQ_ROWS + " x " + REQ_COLS + ".", "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		return true;
	}
}
