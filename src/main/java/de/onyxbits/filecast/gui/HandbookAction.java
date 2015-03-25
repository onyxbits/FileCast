package de.onyxbits.filecast.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class HandbookAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HandbookAction() {
		putValue(NAME, Messages.getString("HandbookAction.0")); //$NON-NLS-1$
		putValue(MNEMONIC_KEY, KeyStroke
				.getKeyStroke(Messages.getString("HandbookAction.1")).getKeyCode()); //$NON-NLS-1$
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URI(Messages.getString("HandbookAction.2")));
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
