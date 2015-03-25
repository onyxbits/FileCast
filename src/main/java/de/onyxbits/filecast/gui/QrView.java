package de.onyxbits.filecast.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.JPanel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QrView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final BufferedImage NOTHING = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	private BufferedImage image;

	public QrView() {
		clear();
	}

	public void clear() {
		image = NOTHING;
		repaint();
	}

	public void setContentString(String content) throws WriterException {
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, getWidth(),
				getWidth(), hintMap);
		int size = byteMatrix.getWidth();

		image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, size, size);
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		repaint();
	}

	@Override
	public void paint(Graphics gr) {
		gr.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}
}
