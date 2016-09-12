package org.magic.gui.game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.magic.api.beans.MagicCard;
import org.magic.game.PositionEnum;
import org.magic.services.MagicFactory;

public class LibraryPanel extends DraggablePanel {

	Image i;
	
	public LibraryPanel() {
		super();
		
		try {
			i=MagicFactory.getInstance().getEnabledPicturesProvider().getBackPicture().getScaledInstance(179, 240, BufferedImage.SCALE_SMOOTH);
				setPreferredSize(new Dimension(i.getWidth(null), i.getHeight(null)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	DisplayableCard addedCard;
	@Override
	public void addComponent(DisplayableCard i) {
		add(i);
		addedCard=i;
	}
	
	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		
		switch (to) {
			case BATTLEFIELD:player.playCardFromLibrary(mc);break;
			case EXIL:player.exileCardFromLibrary(mc);break;
			case HAND:player.searchCardFromLibrary(mc);break;
			case LIBRARY:player.reoderCardInLibrary(mc, true);
			
		default:break;
	}
		
		
	}
	
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.LIBRARY;
	}
	
	
	@Override
	public void paint(Graphics g) {
		
		try {
			g.drawImage(i, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



	@Override
	public void postTreatment() {
	remove(addedCard);
		
	}
}
