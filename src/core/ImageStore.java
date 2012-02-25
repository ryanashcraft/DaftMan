package core;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages images for use in the game.
 */
public class ImageStore {
	private static ImageStore imageStore;
	private static MediaTracker mediaTracker;
	
	public final Map<String, Image> IMAGES;
	public final Map<String, Image[]> ANIMATIONS;
	
	/**
	 * Constructs a new {@code ImageStore} and initializes it.
	 * 
	 * @param component Component that the images are loading for
	 */
	private ImageStore(Component component) {
		imageStore = this;
		mediaTracker = new MediaTracker(component);
		
		IMAGES = new HashMap<String, Image>();
		ANIMATIONS = new HashMap<String, Image[]>();
		
		initialize();
	}
	
	/**
	 * Initializes the images in the store.
	 */
	private void initialize() {
		int imageID = 0;
				
		addImage("images/wall.png", "WALL", imageID++);
		
		try {
        	mediaTracker.waitForAll();
        } catch (InterruptedException e) {
        	System.err.println("Could not load all images into ImageStore.");
        	e.printStackTrace();
            return;
        }
	}
	
	/**
	 * Adds an {@code Image} to the store with a path, name and id.
	 * 
	 * @param path Relative path to image
	 * @param name Name of image to which you wish to refer to it by
	 * @param id Unique id of image
	 */
	private void addImage(String path, String name, int id) {
		Image image = Toolkit.getDefaultToolkit().createImage(path);
		
		IMAGES.put(name, image);
		mediaTracker.addImage(image, id);
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
	public static ImageStore get(Component component) {
		if (imageStore == null) {
			imageStore = new ImageStore(component);
		}

		return imageStore;
	}
}
