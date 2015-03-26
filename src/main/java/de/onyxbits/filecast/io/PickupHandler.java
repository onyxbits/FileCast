package de.onyxbits.filecast.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class PickupHandler extends AbstractHandler {

	private MimeTypes mimeTypes = new MimeTypes();
	private DateFormat timeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
			DateFormat.MEDIUM);
	private Store store;
	private boolean zebra;
	private Logger logger;

	public PickupHandler(Store store) {
		this.store = store;
		logger = Log.getLogger(getClass());
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		File requested = store.lookup(getKey(target));
		response.setStatus(HttpServletResponse.SC_OK);

		// Handle classpath resources
		InputStream ins = getClass().getClassLoader().getResourceAsStream("rsrc" + target);
		if (ins != null) {
			byte[] b = IOUtils.toByteArray(ins);
			response.setHeader("Content-Length", new Long(b.length).toString());
			ins.close();
			ins = getClass().getClassLoader().getResourceAsStream("rsrc" + target);
			IOUtils.copy(ins, response.getOutputStream());
			ins.close();
			return;
		}

		// Can't handle -> bail
		if (requested == null || !requested.canRead()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "403 - FORBIDDEN");
			logger.info("FORBIDDEN: " + baseRequest.getRemoteAddr() + "-> " + target);
			baseRequest.setHandled(true);
			penalize();
			return;
		}

		// Handle File Download
		if (requested.isFile()) {
			response.setHeader("Content-Length", new Long(requested.length()).toString());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + requested.getName()+"\"");
			Buffer mime = mimeTypes.getMimeByExtension(requested.getName());
			if (mime != null) {
				response.setContentType(mime.toString());
			}
			baseRequest.setHandled(true);
			logger.info("BEGIN: " + baseRequest.getRemoteAddr() + " -> " + requested.getPath());
			IOUtils.copy(new FileInputStream(requested), response.getOutputStream());
			logger.info("FINISHED: " + baseRequest.getRemoteAddr() + " -> " + requested.getPath());
		}
		
		// Handle Directory listing
		if (requested.isDirectory()) {
			logger.info("LIST: " + baseRequest.getRemoteAddr() + " -> " + requested.getPath());
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter pw = response.getWriter();
			pw.write("<html><link rel=\"icon\" type=\"image/png\" href=\"favicon.png\"><link rel=\"stylesheet\" href=\"style.css\"><body>");
			File[] lst = requested.listFiles(new HiddenFileFilter());
			String parent = store.reverseLookup(requested.getParentFile());
			Arrays.sort(lst, new FileComparator());
			pw.write("<h1>" + requested.getName() + "</h1>");
			pw.write("<table>");
			pw.write(makeHeader());
			if (parent != null) {
				pw.write(makeRow(requested.getParentFile(), ".."));
			}
			for (File f : lst) {
				pw.write(makeRow(f, null));
			}
			pw.write("</table></body></html>");
			baseRequest.setHandled(true);
		}
	}

	private String makeHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("<th>");
		sb.append(Messages.getString("PickupHandler.name"));
		sb.append("</th>");
		sb.append("<th>");
		sb.append(Messages.getString("PickupHandler.size"));
		sb.append("</th>");
		sb.append("<th>");
		sb.append(Messages.getString("PickupHandler.date"));
		sb.append("</th>");
		return sb.toString();
	}

	public static String pathFor(String key) {
		return "/" + key;
	}

	private String makeRow(File content, String nameOverride) {
		StringBuilder sb = new StringBuilder();
		if (content != null) {
			String key = store.register(content);
			sb.append("<tr ");
			if (zebra) {
				sb.append("class=\"evenrow\">");
			}
			else {
				sb.append("class=\"oddrow\">");
			}
			sb.append("<td>");
			sb.append("<a href=\"/");
			sb.append(key);
			sb.append("\">");
			if (content.isDirectory()) {
				sb.append("<img src=\"directory.png\"> ");
			}
			else {
				sb.append("<img src=\"file.png\"> ");
			}
			if (nameOverride != null) {
				sb.append(nameOverride);
			}
			else {
				sb.append(content.getName());
			}
			sb.append("</a>");
			sb.append("</td>");
			sb.append("<td>");
			if (content.isDirectory()) {
				sb.append("-");
			}
			else {
				sb.append(humanReadableByteCount(content.length(), true));
			}
			sb.append("</td>");
			sb.append("<td>");
			sb.append(timeFormat.format(content.lastModified()));
			sb.append("</td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}

	private String getKey(String target) {
		try {
			return target.split("/")[1];
		}
		catch (Exception e) {
		}
		return "";
	}

	private void penalize() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turn a raw filesize into a human readable one
	 * 
	 * @param bytes
	 *          raw size
	 * @param si
	 *          use SI units
	 * @return formated string.
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
