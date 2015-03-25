package de.onyxbits.filecast.gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextPane;


/**
 * A Panel for showing content.
 * 
 * @author patrick
 * 
 */
class ContentView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected QrView qrView;

	protected JTextPane uri;

	public ContentView() {
		qrView = new QrView();
		uri = new JTextPane();
		uri.setBackground(null);
		uri.setBorder(null);
		uri.setEditable(false);
		uri.setContentType("text/plain");
		uri.setFont(new Font("monospaced", Font.PLAIN, 14));
		qrView.setPreferredSize(new Dimension(300, 300));
		add(qrView);
		add(uri);
	}
}
