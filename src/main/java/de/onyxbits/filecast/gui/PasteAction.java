package de.onyxbits.filecast.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class PasteAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MainWindow mainWindow;

	public PasteAction() {
		putValue(NAME, Messages.getString("PasteAction.0")); //$NON-NLS-1$
		putValue(MNEMONIC_KEY, KeyStroke.getKeyStroke(Messages.getString("PasteAction.1")).getKeyCode()); //$NON-NLS-1$
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			URI uri = new URI(cb.getData(DataFlavor.stringFlavor).toString().trim());
			mainWindow.selectFile(new File(uri));
		}
		catch (Exception exp) {
		}

		try {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			File f = new File(cb.getData(DataFlavor.stringFlavor).toString().trim());
			if (f.exists()) {
				mainWindow.selectFile(f);
			}
		}
		catch (Exception exp) {
		}
	}
}
