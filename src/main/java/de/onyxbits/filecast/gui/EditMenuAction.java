package de.onyxbits.filecast.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class EditMenuAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditMenuAction() {
		putValue(NAME, Messages.getString("EditMenuAction.0")); //$NON-NLS-1$
		putValue(MNEMONIC_KEY, KeyStroke
				.getKeyStroke(Messages.getString("EditMenuAction.1")).getKeyCode()); //$NON-NLS-1$
	}

	public void actionPerformed(ActionEvent arg0) {

	}

}
