package de.onyxbits.filecast.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.onyxbits.filecast.App;
import de.onyxbits.filecast.io.PickupHandler;
import de.onyxbits.filecast.io.Store;

public class MainWindow extends JFrame implements Runnable, TreeSelectionListener, ActionListener {

	private static final String[] ICONRESOURCES = { "appicon-16.png", //$NON-NLS-1$
			"appicon-24.png", //$NON-NLS-1$
			"appicon-32.png", //$NON-NLS-1$
			"appicon-48.png", //$NON-NLS-1$
			"appicon-64.png", //$NON-NLS-1$
			"appicon-96.png", //$NON-NLS-1$
			"appicon-128.png" }; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	private JMenuBar menuBar;
	private ContentView contentView;
	private JTree directory;
	private JTextField location;
	private CopyAction copyAction;
	private PasteAction pasteAction;
	private Store store;

	public MainWindow(File initial, Store store) {
		this.store = store;
		copyAction = new CopyAction();
		pasteAction = new PasteAction();
		CodeAction codeAction = new CodeAction();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu(new FileMenuAction());
		JMenuItem quitItem = new JMenuItem(new QuitAction());
		fileMenu.add(quitItem);
		menuBar.add(fileMenu);
		JMenu editMenu = new JMenu(new EditMenuAction());
		editMenu.add(new JMenuItem(codeAction));
		editMenu.add(new JSeparator());
		editMenu.add(new JMenuItem(copyAction));
		editMenu.add(new JMenuItem(pasteAction));
		menuBar.add(editMenu);
		JMenu helpMenu = new JMenu(new HelpMenuAction());
		helpMenu.add(new JMenuItem(new HandbookAction()));
		menuBar.add(helpMenu);

		FileTreeNode ftn = new FileTreeNode(File.listRoots());
		directory = new JTree(ftn);
		directory.setCellRenderer(new FileTreeCellRenderer());
		directory.setRootVisible(false);
		directory.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		directory.setPreferredSize(null);
		contentView = new ContentView();
		codeAction.contentView=contentView;
		directory.getActionMap().put("copy", copyAction);

		location = new JTextField(initial.getAbsolutePath());
		location.setMargin(new Insets(3, 3, 3, 3));
		JScrollPane treeScroll = new JScrollPane(directory);
		JScrollPane contentScroll = new JScrollPane(contentView);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, contentScroll);
		splitPane.setResizeWeight(0.45d);

		JPanel content = new JPanel();
		JPanel locationBar = new JPanel();
		locationBar.setLayout(new BoxLayout(locationBar, BoxLayout.X_AXIS));
		locationBar.add(new JLabel(Messages.getString("MainWindow.7"))); //$NON-NLS-1$
		locationBar.add(Box.createHorizontalStrut(5));
		locationBar.add(location);
		locationBar.setBorder(new EmptyBorder(8, 5, 8, 5));
		content.setLayout(new BorderLayout());
		content.add(splitPane, BorderLayout.CENTER);
		content.add(locationBar, BorderLayout.NORTH);
		setContentPane(content);
		content.setBorder(new EmptyBorder(0,3,1,3));
		List<Image> icons = new Vector<Image>();
		for (String ico : ICONRESOURCES) {
			try {
				icons.add(new ImageIcon(ClassLoader.getSystemResource(ico), "").getImage()); //$NON-NLS-1$
			}
			catch (Exception e) {
				// No need to make a fuss if we don't have all possible resolutions.
			}
		}
		setIconImages(icons);
		selectFile(initial);
	}

	/**
	 * Programmatically select a file
	 * 
	 * @param file
	 *          the file to display
	 */
	public void selectFile(File file) {
		FileTreeNode ftn = (FileTreeNode) directory.getModel().getRoot();
		ftn = ftn.searchFor(file);
		TreePath tp = ftn.getPath();
		directory.setSelectionPath(tp);
		directory.scrollPathToVisible(tp);
		if (file.isDirectory()) {
			directory.expandPath(tp);
		}
	}

	public void run() {
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		directory.addTreeSelectionListener(this);
		location.addActionListener(this);
		pasteAction.mainWindow = this;
		location.getActionMap().put("paste", pasteAction);
		directory.getActionMap().put("paste", pasteAction);
		// The first call to selectPath() is IO heavy, so we do it in the
		// constructor to keep it off the EDT. However, this means we need to
		// re-select the path here in order to for the UI to catch up.
		TreePath sel = directory.getSelectionPath();
		directory.setSelectionPath(null);
		directory.setSelectionPath(sel);
	}

	public void valueChanged(TreeSelectionEvent e) {
		try {
			FileTreeNode ftn = (FileTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
			store.clear();
			String url = "http://" + System.getProperty(App.SYSPROP_ADDR) + ":"
					+ System.getProperty(App.SYSPROP_PORT) + PickupHandler.pathFor(store.register(ftn.file));
			contentView.qrView.setContentString(url);
			contentView.textView.setText(url);
			copyAction.value = url;
			setTitle(ftn.file.getPath() + " - Filecast");
			location.setText(ftn.file.getAbsolutePath());
			location.setCaretPosition(location.getText().length());
		}
		catch (Exception e1) {
			contentView.textView.setText("");
			contentView.qrView.clear();
			copyAction.value = "";
			setTitle("Filecast");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == location) {
			File f = new File(location.getText());
			if (f.exists()) {
				selectFile(f);
			}
		}
	}

}
