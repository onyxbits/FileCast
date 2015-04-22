package de.onyxbits.filecast;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.SwingUtilities;

import org.eclipse.jetty.server.Server;

import de.onyxbits.filecast.gui.MainWindow;
import de.onyxbits.filecast.io.PickupHandler;
import de.onyxbits.filecast.io.Store;

public class App implements Runnable {

	/**
	 * The system property that contains the server address.
	 */
	public static final String SYSPROP_ADDR = "address";

	/**
	 * The system property that contains the server port.
	 */
	public static final String SYSPROP_PORT = "port";

	private App(Store store, File initial) {
		this.store = store;
		this.initial = initial;
	}

	private MainWindow mainWindow;
	private File initial;
	private Store store;

	/**
	 * @param args
	 *          File/Directory to serve initially
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Store store = new Store();
		File initial = new File(System.getProperty("user.dir"));
		try {
			File tmp = new File(args[0]);
			if (tmp.exists()) {
				initial = tmp.getAbsoluteFile();
			}
		}
		catch (Exception e) {
		}

		App app = new App(store, initial);

		if (System.getProperty(SYSPROP_ADDR) == null) {
			try {
				// Autodiscover a public IP address by making a pseudo connection to an
				// outside host and checking which network interface the OS would like
				// to route it through. This is not guaranteed to work, but in setups
				// where it doesn't, the user probably knows why and can force an
				// address.

				if (System.getProperty("java.net.preferIPv4Stack") == null) {
					// Dirty hack! Prevent autodection of an IPv6 address unless the user
					// absolutely wants it.
					System.setProperty("java.net.preferIPv4Stack", "true");
				}

				DatagramSocket s = new DatagramSocket();
				s.connect(InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }), 0);
				InetAddress tmp = s.getLocalAddress();
				s.close();
				if (tmp.getAddress().length > 8) {
					System.setProperty(SYSPROP_ADDR, "[" + tmp.getHostAddress() + "]");
				}
				else {
					System.setProperty(SYSPROP_ADDR, tmp.getHostAddress());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.setProperty(SYSPROP_ADDR, "unknown-address");
			}
		}
		int port = 0;
		try {
			port = Integer.parseInt(System.getProperty(SYSPROP_PORT));
		}
		catch (Exception e) {
			// Can't parse -> autodetect
		}

		// Starting a server takes time, building the directory tree takes time.
		// Both are independent of each other, so we go through this bit of extra
		// hassle for faster startup.
		Thread t = new Thread(app);
		t.start();
		Server server = new Server(port);
		server.setHandler(new PickupHandler(store));
		server.start();
		t.join(); // Block till the server is operational and has assigned a port.
		System.setProperty("port", server.getConnectors()[0].getLocalPort() + "");
		SwingUtilities.invokeLater(app.mainWindow);
	}

	public void run() {
		mainWindow = new MainWindow(initial, store);
	}
}
