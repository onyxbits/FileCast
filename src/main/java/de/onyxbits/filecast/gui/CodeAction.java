package de.onyxbits.filecast.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

class CodeAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String value;

	public ContentView contentView;

	public CodeAction() {
		putValue(NAME, Messages.getString("CodeAction.0")); //$NON-NLS-1$
		putValue(MNEMONIC_KEY, KeyStroke.getKeyStroke(Messages.getString("CodeAction.1")).getKeyCode()); //$NON-NLS-1$
	}

	public void actionPerformed(ActionEvent e) {
		try {
			contentView.qrView.setContentString(contentView.textView.getText());
		}
		catch (Exception exp) {
			//exp.printStackTrace();
		}
	}

}
