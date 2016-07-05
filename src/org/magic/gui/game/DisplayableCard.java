package org.magic.gui.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.transfert.CardTransfertHandler;


public class DisplayableCard extends JLabel  {

	
	private MagicCard magicCard;
	private boolean tapped=false;
	private ImageIcon image;
	private boolean draggable=true;
	
	
	public ImageIcon getImageIcon() {
		return image;
	}


	public void setImage(ImageIcon image) {
		this.image = image;
	}
	private String title;
	private String bottom;
	
	private CardTransfertHandler dndHandler;
	
	
	public CardTransfertHandler getDndHandler() {
		return dndHandler;
	}


	public void setDndHandler(CardTransfertHandler dndHandler) {
		this.dndHandler = dndHandler;
	}



	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
		setText(title);
	}


	public String getBottom() {
		return bottom;
	}


	public void setBottom(String bottom) {
		this.bottom = bottom;
	}


	public boolean isDraggable() {
		return draggable;
	}


	public void enableDrag(boolean drag) {
		this.draggable = drag;
	}


	public DisplayableCard(MagicCard mc,int width,int height) {
		setSize(width, height);
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		magicCard=mc;
		
		dndHandler = new CardTransfertHandler();
		setTransferHandler(dndHandler);
		
		
		URL url;
		try {
			url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getEditions().get(0).getMultiverse_id()+"&type=card");
			image = new ImageIcon(ImageIO.read(url).getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}
	
	public void tap(boolean t) {
			
			int angle=0;
			if(t)
				angle=90;
			else
				angle=-90;
		
	        int w = getWidth();
	        int h = getHeight();
	        int type = BufferedImage.TYPE_INT_RGB;  // other options, see api
	        BufferedImage image = new BufferedImage(h, w, type);
	        Graphics2D g2 = image.createGraphics();
	        double x = (h - w)/2.0;
	        double y = (w - h)/2.0;
	        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
	        at.rotate(Math.toRadians(angle), w/2.0, h/2.0);
	        g2.drawImage(getImageIcon().getImage(), at,null);
	        g2.dispose();
	        this.setIcon(new ImageIcon(image));
	}
	
	
	@Override
	public Icon getIcon() {
		
		if(magicCard != null)
			return image;
		
		return super.getIcon();
	}
	
	
	public MagicCard getMc() {
		return magicCard;
	}
	public void setMc(MagicCard mc) {
		this.magicCard = mc;
	}
	public boolean isTapped() {
		return tapped;
	}
	public void setTapped(boolean tapped) {
		this.tapped = tapped;
	}



		
}
