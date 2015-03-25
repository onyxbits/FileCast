package de.onyxbits.filecast.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class HelpMenuAction extends AbstractAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelpMenuAction() {
		putValue(NAME, Messages.getString("HelpMenuAction.0")); //$NON-NLS-1$
		putValue(MNEMONIC_KEY, KeyStroke
				.getKeyStroke(Messages.getString("HelpMenuAction.1")).getKeyCode()); //$NON-NLS-1$
	}

	public void actionPerformed(ActionEvent arg0) {

	}

}
