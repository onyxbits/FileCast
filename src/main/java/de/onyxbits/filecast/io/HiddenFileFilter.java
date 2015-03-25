package de.onyxbits.filecast.io;

import java.io.File;
import java.io.FileFilter;

public class HiddenFileFilter implements FileFilter {

	public static boolean showHidden = false;

	public boolean accept(File file) {
		return (!file.isHidden() || showHidden);
	}
}
