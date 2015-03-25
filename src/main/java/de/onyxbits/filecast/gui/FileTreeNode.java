package de.onyxbits.filecast.gui;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.onyxbits.filecast.io.FileComparator;
import de.onyxbits.filecast.io.HiddenFileFilter;

class FileTreeNode implements TreeNode {

	/**
	 * Node file.
	 */
	protected File file;

	/**
	 * Children of the node file.
	 */
	private File[] children;

	private FileTreeNode[] nodeChildren;

	/**
	 * Parent node.
	 */
	private FileTreeNode parent;

	/**
	 * Indication whether this node corresponds to a file system root.
	 */
	protected boolean isFileSystemRoot;

	/**
	 * Creates a new file tree node.
	 * 
	 * @param file
	 *          Node file
	 * @param isFileSystemRoot
	 *          Indicates whether the file is a file system root.
	 * @param parent
	 *          Parent node.
	 */
	public FileTreeNode(File file, boolean isFileSystemRoot, FileTreeNode parent) {
		this.file = file;
		this.isFileSystemRoot = isFileSystemRoot;
		this.parent = parent;
		this.children = file.listFiles(new HiddenFileFilter());
		if (this.children == null) {
			this.children = new File[0];
		}
		nodeChildren = new FileTreeNode[children.length];
		Arrays.sort(children, new FileComparator());
	}

	/**
	 * Creates a new file tree node.
	 * 
	 * @param children
	 *          Children files.
	 */
	public FileTreeNode(File[] children) {
		this.file = null;
		this.parent = null;
		this.children = children;
		nodeChildren = new FileTreeNode[children.length];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#children()
	 */
	public Enumeration<?> children() {
		return new NodeEnumeration(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getChildAt(int)
	 */
	public TreeNode getChildAt(int childIndex) {
		if (nodeChildren[childIndex] == null) {
			nodeChildren[childIndex] = new FileTreeNode(this.children[childIndex], this.parent == null,
					this);
		}
		return nodeChildren[childIndex];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	public int getChildCount() {
		return children.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	public int getIndex(TreeNode node) {
		FileTreeNode ftn = (FileTreeNode) node;
		for (int i = 0; i < this.children.length; i++) {
			if (ftn.file.equals(this.children[i]))
				return i;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	public TreeNode getParent() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return (children.length == 0);
	}

	public TreePath getPath() {
		return new TreePath(getPathToRoot(this, 0));
	}

	private FileTreeNode[] getPathToRoot(FileTreeNode node, int depth) {
		if (node == null) {
			if (depth == 0) {
				return null;
			}
			return new FileTreeNode[depth];
		}
		FileTreeNode[] path = getPathToRoot(node.parent, depth + 1);
		path[path.length - depth - 1] = node;
		return path;
	}

	/**
	 * Find a file in the hierarchy
	 * 
	 * @param file
	 *          the file to look for
	 * @return the node containing the file or null if not found
	 */
	public FileTreeNode searchFor(File f) {
		Vector<File> collect = new Vector<File>();
		File tmp = f;
		while (tmp != null) {
			collect.add(tmp);
			tmp = tmp.getParentFile();
		}
		File[] path = collect.toArray(new File[0]);
		return searchFor(path, path.length - 1);
	}

	private FileTreeNode searchFor(File[] path, int depth) {
		if (file != null && file.equals(path[0])) {
			// This node contains the file in question.
			return this;
		}
		if (children.length > 0) { // Only descent into directories
			// Don't descent into branches that cannot contain the file
			if (file == null || file.equals(path[depth])) {
				for (int i = 0; i < children.length; i++) {
					FileTreeNode ftn = (FileTreeNode) getChildAt(i);
					if (file == null) { // A Filesystem node
						ftn = ftn.searchFor(path, depth);
					}
					else { // A tree node
						ftn = ftn.searchFor(path, depth - 1);
					}
					if (ftn != null) {
						// A subnode contained the file -> transport all the way up.
						return ftn;
					}
				}
			}
		}
		// We neither have the file nor a subtree that might have it.
		return null;
	}
}
