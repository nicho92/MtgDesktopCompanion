package org.magic.gui.components.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.magic.services.tools.ImageTools;

public class CropImagePanel extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int dragStatus = 0;
	private int c1;
	private int c2;
	private int c3;
	private int c4;
	private transient Image selectedImage;

	@Override
	public void paintComponent(Graphics g) {
		if (selectedImage != null) {

			g.drawImage(selectedImage, 0, 0, getWidth(), getHeight(), null);
		}
	}

	public void setImage(Image i) {
		selectedImage = i;
		validate();
		repaint();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	

	public Rectangle getCroppedDimension()
	{

		int w = c1 - c3;
		int h = c2 - c4;
		w = w * -1;
		h = h * -1;
		return new Rectangle(c1, c2, w, h);
	}


	public BufferedImage getCroppedImage() {
		try {
			int w = c1 - c3;
			int h = c2 - c4;
			w = w * -1;
			h = h * -1;
			var img = ImageTools.imageToBufferedImage(selectedImage);
			return img.getSubimage(c1, c2, w, h);
		} catch (Exception _) {
			return null;
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		repaint();
		c1 = arg0.getX();
		c2 = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		repaint();
		if (dragStatus == 1) {
			c3 = arg0.getX();
			c4 = arg0.getY();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		repaint();
		dragStatus = 1;
		c3 = arg0.getX();
		c4 = arg0.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// do nothing
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int w = c1 - c3;
		int h = c2 - c4;
		w = w * -1;
		h = h * -1;
		if (w < 0)
			w = w * -1;

		g.setColor(Color.red);
		g.drawRect(c1, c2, w, h);
	}
}
