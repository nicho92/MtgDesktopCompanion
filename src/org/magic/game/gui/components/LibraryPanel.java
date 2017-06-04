package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;

import org.magic.api.beans.MagicCard;
import org.magic.game.actions.cards.ScryActions;
import org.magic.game.actions.library.DrawActions;
import org.magic.game.actions.library.DrawHandActions;
import org.magic.game.actions.library.MoveGraveyardActions;
import org.magic.game.actions.library.SearchActions;
import org.magic.game.actions.library.ShuffleActions;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;

public class LibraryPanel extends DraggablePanel {

	Image i;
	
	public LibraryPanel() {
		super();
		menu.add(new JMenuItem(new DrawHandActions()));
		menu.add(new JMenuItem(new DrawActions()));
		menu.add(new JMenuItem(new SearchActions()));
		menu.add(new JMenuItem(new ScryActions(null)));
		menu.add(new JMenuItem(new ShuffleActions()));
		menu.add(new JMenuItem(new MoveGraveyardActions()));
		
		try {
			i=MTGControler.getInstance().getEnabledPicturesProvider().getBackPicture().getScaledInstance((int)MTGControler.getInstance().getCardsDimension().getWidth(),(int)MTGControler.getInstance().getCardsDimension().getHeight(), BufferedImage.SCALE_SMOOTH);
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
		addedCard.setPosition(getOrigine());
	}
	
	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		
		switch (to) {
			case BATTLEFIELD:player.playCardFromLibrary(mc);break;
			case EXIL:player.exileCardFromLibrary(mc);break;
			case HAND:player.searchCardFromLibrary(mc);break;
			case LIBRARY:player.reoderCardInLibrary(mc, true);
			case GRAVEYARD:player.discardCardFromLibrary(mc);
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
