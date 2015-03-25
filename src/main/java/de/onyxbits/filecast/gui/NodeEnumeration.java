package de.onyxbits.filecast.gui;

import java.util.Enumeration;

class NodeEnumeration implements Enumeration<FileTreeNode> {

	private FileTreeNode node;
	private int index;

	public NodeEnumeration(FileTreeNode node) {
		this.node = node;
	}

	public boolean hasMoreElements() {
		return index < node.getChildCount();
	}

	public FileTreeNode nextElement() {
		FileTreeNode ret = (FileTreeNode) node.getChildAt(index);
		index++;
		return ret;
	}

}
