package org.magic.gui.game;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.transfert.CardTransfertHandler;
import org.magic.services.games.PositionEnum;


public class DisplayableCard extends JLabel {

	
	private MagicCard magicCard;
	private boolean tapped=false;
	private ImageIcon image;
	private boolean draggable=true;
	private PositionEnum origine;
	
	
	private String title;
	private String bottom;
	
	private CardTransfertHandler dndHandler;
	
	
	public CardTransfertHandler getDndHandler() {
		return dndHandler;
	}


	public void setDndHandler(CardTransfertHandler dndHandler) {
		this.dndHandler = dndHandler;
	}


	public PositionEnum getOrigine() {
		return origine;
	}


	public void setOrigine(PositionEnum origine) {
		this.origine = origine;
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
