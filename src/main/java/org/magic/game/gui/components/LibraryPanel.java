package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;

import org.magic.game.actions.cards.ScryActions;
import org.magic.game.actions.library.DrawActions;
import org.magic.game.actions.library.DrawHandActions;
import org.magic.game.actions.library.MoveGraveyardActions;
import org.magic.game.actions.library.ShuffleActions;
import org.magic.game.actions.player.SearchActions;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class LibraryPanel extends DraggablePanel {

	Image i;
	
	public LibraryPanel() {
		super();
		menu.add(new JMenuItem(new DrawHandActions()));
		menu.add(new JMenuItem(new DrawActions()));
		menu.add(new JMenuItem(new SearchActions(getOrigine())));
		menu.add(new JMenuItem(new ScryActions(null)));
		menu.add(new JMenuItem(new ShuffleActions()));
		menu.add(new JMenuItem(new MoveGraveyardActions()));
		
		try {
			i=MTGControler.getInstance().getEnabledPicturesProvider().getBackPicture().getScaledInstance((int)MTGControler.getInstance().getCardsDimension().getWidth(),(int)MTGControler.getInstance().getCardsDimension().getHeight(), BufferedImage.SCALE_SMOOTH);
				setPreferredSize(new Dimension(i.getWidth(null), i.getHeight(null)));
			
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
		}
	}
	
	
	@Override
	public void addComponent(DisplayableCard i) {
		add(i);
		i.setPosition(getOrigine());
	}
	
	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {
		
		switch (to) {
			case BATTLEFIELD:player.playCardFromLibrary(mc.getMagicCard());break;
			case EXIL:player.exileCardFromLibrary(mc.getMagicCard());break;
			case HAND:player.searchCardFromLibrary(mc.getMagicCard());break;
			case LIBRARY:player.reoderCardInLibrary(mc.getMagicCard(), true);
			case GRAVEYARD:player.discardCardFromLibrary(mc.getMagicCard());
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
			MTGLogger.printStackTrace(e);
		}
		
	}

	@Override
	public void postTreatment(DisplayableCard c) {
		remove(c);
		
	}
}
