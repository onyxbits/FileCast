package de.onyxbits.filecast.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class BookmarksMenuAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BookmarksMenuAction() {
		putValue(NAME,"Bookmarks");
		putValue(MNEMONIC_KEY,KeyStroke.getKeyStroke("B").getKeyCode());
	}
	
	public void actionPerformed(ActionEvent arg0) {

	}

}
