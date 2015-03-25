package de.onyxbits.filecast.io;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Central registry for keeping track (and providing access control) for what
 * files are shared over the network.
 * 
 * @author patrick
 * 
 */
public class Store {

	private HashMap<String, File> mappings;
	private HashMap<File, String> reverse;
	private Random rng;
	private Logger logger;

	private static final char[] SYMBOLS;

	static {
		StringBuilder tmp = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			tmp.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			tmp.append(ch);
		SYMBOLS = tmp.toString().toCharArray();
	}

	public Store() {
		mappings = new HashMap<String, File>();
		reverse = new HashMap<File, String>();
		rng = new Random(System.currentTimeMillis());
		logger = Log.getLogger(getClass());
	}

	/**
	 * Register a file for sharing
	 * 
	 * @param file
	 *          the file to share. May be a directory.
	 * @return a unique, randomly generated key by which the file may be retrieved
	 *         until it gets unregistered.
	 */
	public synchronized String register(File file) {
		String ret = reverse.get(file);
		if (ret != null) {
			return ret;
		}
		do {
			char[] buf = new char[6];
			for (int idx = 0; idx < buf.length; ++idx) {
				buf[idx] = SYMBOLS[rng.nextInt(SYMBOLS.length)];
			}
			ret = new String(buf);
		}
		while (reverse.containsKey(ret));
		mappings.put(ret, file);
		reverse.put(file, ret);
		logger.info("REGISTER "+ret+" -> "+file.getPath());
		return ret;
	}

	/**
	 * clear all registrations.
	 */
	public void clear() {
		mappings.clear();
		reverse.clear();
	}

	/**
	 * Lookup a file by its key.
	 * 
	 * @param key
	 *          the key
	 * @return the requested file or null if not registered.
	 */
	public File lookup(String key) {
		return mappings.get(key);
	}

	/**
	 * Lookup the key for a file
	 * 
	 * @param key
	 *          the key
	 * @return the file or null if not registered.
	 */
	public String reverseLookup(File key) {
		return reverse.get(key);
	}

}
