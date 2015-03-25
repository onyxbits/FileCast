package de.onyxbits.filecast.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
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

	protected JTextPane textView;

	public ContentView() {
		qrView = new QrView();
		textView = new JTextPane();
		textView.setContentType("text/plain");
		textView.setFont(new Font("monospaced", Font.PLAIN, 14));
		qrView.setPreferredSize(new Dimension(350, 350));
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx=1;
		gbc.weighty=0.5d;
		add(qrView,gbc);
		
		gbc.weightx=0;
		gbc.weighty=0.5d;
		gbc.gridy=1;
		add(textView,gbc);
	}
}
