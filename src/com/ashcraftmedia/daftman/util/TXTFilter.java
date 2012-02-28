package com.ashcraftmedia.daftman.util;
import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * HW10: DAFTMAN
 * I worked on this assignment alone, using course materials, previous work,
 * and the online Java API.
 *  
 * TXTFilter.java
 * This class extends FileFilter to define that only files with txt
 * extensions or directories can be opened. Also sets the description of these files.
 * 
 * @author Ryan Ashcraft
 * @version 1.0 11/15/2010
 */

public class TXTFilter extends FileFilter {
	/**
	 * Returns whether a file or directory can be opened. If the file has
	 * a txt extension or is a directory, it will return true.
	 * 
	 * @param file The file
	 * @return The boolean value whether the file can be opened
	 */
	public boolean accept(File file) {
		if (file.getName().toLowerCase().endsWith(".txt") || file.isDirectory()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets the description for types of files that can be opened.
	 * 
	 * @return The string description of types of files that can be opened
	 */
	public String getDescription() {
		return "TXT files (*.txt)";
	}
}