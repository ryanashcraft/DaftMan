package com.ashcraftmedia.daftman.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundStore {
	private static SoundStore soundStore;
	private static Sequencer sequencer;
	private static boolean muted;
	
	public final Map<String, Object> SOUNDS;
	
	/**
	 * Constructs a new {@code SoundStore} and initializes it.
	 */
	private SoundStore() {
		soundStore = this;
		
		SOUNDS = new HashMap<String, Object>();
		
		try {
			initialize();
		} catch (IOException e) {
			System.err.println("Error loading sound files!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the sound in the store.
	 * 
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		addMIDI("sounds/stronger.mid", "STRONGER");
		addMIDI("sounds/aroundtheworld.mid", "AROUND_THE_WORLD");
		addMIDI("sounds/dafunk.mid", "DA_FUNK");
		
		addSound("sounds/explode.wav", "EXPLODE");
		addSound("sounds/fuse.wav", "FUSE");
		addSound("sounds/hurt.wav", "HURT");
		addSound("sounds/rupee-collected.wav", "RUPEE_COLLECTED");
		addSound("sounds/heart.wav", "HEART");
		addSound("sounds/speed-up.wav", "SPEED_UP");
	}
	
	/**
	 * Add a MIDI to the {@code SoundStore}.
	 * 
	 * @param path Path to MIDI file
	 * @param name Name of the sound to refer to it by
	 * @throws IOException
	 */
	private void addMIDI(String path, String name) throws IOException {
	    try {
			Sequence sequence = MidiSystem.getSequence(new File(path));
			
			SOUNDS.put(name, sequence);
		} catch (InvalidMidiDataException e) {
			System.err.println("Invalid MIDI file!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a sound to the {@code SoundStore}.
	 * 
	 * @param path Path to sound file
	 * @param name Name of the sound to refer to it by
	 * @throws IOException
	 */
	private void addSound(String path, String name) throws IOException {
    	try {
			Clip soundClip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path));
			soundClip.open(inputStream);
			
			SOUNDS.put(name, soundClip);
    	} catch (LineUnavailableException e) {
    		System.err.println("Audio not available!");
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			System.err.println("Audio file not supported!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Play a sound.
	 * 
	 * @param name Key of the sound
	 * @param loopCount Number of times to repeat
	 * @param bpm Beat per minute
	 * @param overlay Whether or not to overlay upon any already playing sounds
	 */
	public void playSound(String name, int loopCount, float bpm, boolean overlay) {
		if (muted) {
			return;
		}
		
		Object sound = getSound(name);
		
		if (sound instanceof Sequence) {
			Sequence sequence = (Sequence) sound;
			
			if (!overlay) {
				if (sequencer != null && sequencer.isRunning()) {
					sequencer.stop();
				}
			}
			
		    try {
				sequencer = MidiSystem.getSequencer();
			    sequencer.setLoopCount(loopCount);
			    
			    if ((int)bpm != 0) {
			    	sequencer.setTempoInBPM(bpm);
			    }
			    
			    sequencer.open();
			    sequencer.setSequence(sequence);
		    } catch (MidiUnavailableException e) {
		    	System.err.println("Unable to get access to MIDI!");
		    	e.printStackTrace();
		    } catch (InvalidMidiDataException e) {
		    	System.err.println("Invalid MIDI file!");
		    	e.printStackTrace();
		    }
		    
		    startSequencer();
		} else {
			throw new IllegalArgumentException("Sound file at "+name+" is NOT a sequence!");
		}
	}
	
	/**
	 * Star the sound with name.
	 * 
	 * @param name Key of the sound
	 */
	public void playSound(String name) {
		if (muted) {
			return;
		}
		
		Object sound = getSound(name);
		
		if (sound instanceof Clip) {
			Clip soundClip = (Clip) sound;
			
			soundClip.setFramePosition(0);
			soundClip.start();
		} else {
			throw new IllegalArgumentException("Sound file at "+name+" is NOT a clip!");
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
				sequencer.setTempoInBPM(120.0f);
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
	 * Stop the sound clips.
	 */
	public void stopSoundClips() {
		Collection<Object> sounds = SOUNDS.values();
		for (Object sound : sounds) {
			if (sound instanceof Clip) {
				Clip soundClip = (Clip) sound;
				soundClip.stop();
			}
		}
	}
	
	/**
	 * Close the sequencer.
	 */
	public void closeSequencer() {
		if (sequencer != null) {
			if (sequencer.isOpen()) {
				sequencer.close();
			}
		}
	}
	
	/**
	 * Closes all sound clips
	 */
	public void closeSoundClips() {		
		Collection<Object> sounds = SOUNDS.values();
		for (Object sound : sounds) {
			if (sound instanceof Clip) {
				Clip soundClip = (Clip) sound;
				soundClip.close();
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
			stopSequencer();
			
			Collection<Object> sounds = SOUNDS.values();
			for (Object sound : sounds) {
				if (sound instanceof Clip) {
					Clip soundClip = (Clip) sound;
					if (soundClip.isOpen() && soundClip.isRunning()) {
						soundClip.stop();
					}
				}
			}
		} else {
			startSequencer();
		}
	}
	
	/**
	 * Get the sound for the key.
	 * 
	 * @param name Key for the sound
	 * @return Sound at that key, if any
	 */
	public Object getSound(String name) {		
		return SOUNDS.get(name);
	}
	
	
	/**
	 * Returns the {@code SoundStore} instance. Creates one if one does not exist.
	 * 
	 * @param component Component the sound store is for
	 * @return Instance of SoundStore
	 */
	public static SoundStore get() {
		if (soundStore == null) {
			soundStore = new SoundStore();
		}

		return soundStore;
	}
}
