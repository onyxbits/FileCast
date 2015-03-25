package de.onyxbits.filecast.io;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

	public int compare(File f1, File f2) {
		if (f1.isDirectory() && !f2.isDirectory()) {
			// Directory before non-directory
			return -1;
		}
		else if (!f1.isDirectory() && f2.isDirectory()) {
			// Non-directory after directory
			return 1;
		}
		else {
			// Alphabetic order otherwise
			return f1.compareTo(f2);
		}
	}

}
