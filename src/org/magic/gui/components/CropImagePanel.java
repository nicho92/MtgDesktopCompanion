package org.magic.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.awt.image.ToolkitImage;

public class CropImagePanel extends JPanel implements MouseListener, MouseMotionListener
{
	int drag_status = 0, c1, c2, c3, c4;
	Image selectedImage;
	

	public void paintComponent(Graphics g) 
	{ 
		if(selectedImage!=null)
		{
			
			g.drawImage(selectedImage, 0, 0, getWidth(),getHeight(), null); 
		}
	}
	
	public void setImage(Image i) {
		selectedImage=i;
		validate();
		repaint();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public BufferedImage getCroppedImage() {
		try{
			int w = c1 - c3;
		int h = c2 - c4;
		w = w * -1;
		h = h * -1;
		BufferedImage img = ((ToolkitImage)selectedImage).getBufferedImage();
		return img.getSubimage(c1, c2, w, h);
		}
		catch(Exception nue)
		{
			return null;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
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
		if (drag_status == 1) {
			c3 = arg0.getX();
			c4 = arg0.getY();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		repaint();
		drag_status = 1;
		c3 = arg0.getX();
		c4 = arg0.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

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
