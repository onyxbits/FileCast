package de.onyxbits.filecast.gui;

import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

public class TreeView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private JTextField filter;

	public TreeView(File initial) {
		tree = new JTree();
		filter = new JTextField();
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(new JScrollPane(tree));
		content.add(filter);
		add(content);
	}

}
