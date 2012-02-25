package core;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Manages images for use in the game.
 */
public class ImageStore {
	private static ImageStore imageStore;
	
	public final Map<String, BufferedImage> IMAGES;
	public final Map<String, Image[]> ANIMATIONS;
	
	/**
	 * Constructs a new {@code ImageStore} and initializes it.
	 */
	private ImageStore() {
		imageStore = this;
		
		IMAGES = new HashMap<String, BufferedImage>();
		ANIMATIONS = new HashMap<String, Image[]>();
		
		try {
			initialize();
		} catch (IOException e) {
			System.err.println("Error loading images!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the images in the store.
	 */
	private void initialize() throws IOException {				
		addImage("images/wall.png", "WALL");
		addImage("images/brick.png", "BRICK");
		
		addImage("images/rupee-yellow.png", "YELLOW_RUPEE");
		addImage("images/rupee-blue.png", "BLUE_RUPEE");
		
		addImage("images/star.png", "STAR");
		
		addImage("images/heart.png", "HEART");
		addImage("images/heart-small.png", "SMALL_HEART");
	}
	
	/**
	 * Adds an {@code Image} to the store with a path, name and id.
	 * 
	 * @param path Relative path to image
	 * @param name Name of image to which you wish to refer to it by
	 * @throws IOException
	 */
	private void addImage(String path, String name) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		
		IMAGES.put(name, image);
	}
	
	/**
	 * Get the {@code Image} for the key.
	 * 
	 * @param name Key for the image
	 * @return Image at that key, if any
	 */
	public Image getImage(String name) {
		return IMAGES.get(name);
	}

	/**
	 * Returns the {@code ImageStore} instance. Creates one if one does not exist.
	 * 
	 * @param component Component the image store is for
	 * @return Instance of ImageStore
	 */
	public static ImageStore get() {
		if (imageStore == null) {
			imageStore = new ImageStore();
		}

		return imageStore;
	}
}
