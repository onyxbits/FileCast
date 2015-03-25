package de.onyxbits.filecast.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

class CopyAction extends AbstractAction implements ClipboardOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String value;

	public CopyAction() {
		putValue(NAME, Messages.getString("CopyAction.0")); //$NON-NLS-1$
		putValue(MNEMONIC_KEY, KeyStroke.getKeyStroke(Messages.getString("CopyAction.1")).getKeyCode()); //$NON-NLS-1$
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public void actionPerformed(ActionEvent e) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new StringSelection(value), this);
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

}
