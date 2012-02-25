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
	public final Map<String, BufferedImage[]> ANIMATIONS;
	
	/**
	 * Constructs a new {@code ImageStore} and initializes it.
	 */
	private ImageStore() {
		imageStore = this;
		
		IMAGES = new HashMap<String, BufferedImage>();
		ANIMATIONS = new HashMap<String, BufferedImage[]>();
		
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
		
		addAnimation("images/bro-up-%d.png", "BRO_UP", 3);
		addAnimation("images/bro-down-%d.png", "BRO_DOWN", 3);
		addAnimation("images/bro-left-%d.png", "BRO_LEFT", 3);
		addAnimation("images/bro-right-%d.png", "BRO_RIGHT", 3);
		
		addAnimation("images/foe-up-%d.png", "FOE_UP", 3);
		addAnimation("images/foe-down-%d.png", "FOE_DOWN", 3);
		addAnimation("images/foe-left-%d.png", "FOE_LEFT", 3);
		addAnimation("images/foe-right-%d.png", "FOE_RIGHT", 3);
		
		addAnimation("images/bomb-%d.png", "BOMB", 2);
	}
	
	/**
	 * Adds an {@code Image} to the store with a path and name.
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
	 * Adds an animation to the store with a path, name, and number of images.
	 * 
	 * @param path Relative path to image with %d for each image number
	 * @param name Name of the animation to which you wish to refer to it by
	 * @param numberOfImages Number of images in the animation
	 * @throws IOException
	 */
	private void addAnimation(String path, String name, int numberOfImages) throws IOException {
		BufferedImage[] images = new BufferedImage[numberOfImages];
		
		for (int i = 0; i < images.length; i++) {
			String formattedPath = String.format(path, i + 1);
			images[i] = ImageIO.read(new File(formattedPath));
		}
		
		ANIMATIONS.put(name, images);
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
	 * Get the images for the key.
	 * 
	 * @param name Key for the animation
	 * @return Images at that key, if any
	 */
	public Image[] getAnimation(String name) {
		return ANIMATIONS.get(name);
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
