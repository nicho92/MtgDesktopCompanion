package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.game.actions.battlefield.ChangeBackGroundActions;
import org.magic.game.actions.battlefield.FlipaCoinActions;
import org.magic.game.actions.battlefield.SelectedTapActions;
import org.magic.game.actions.battlefield.UnselectAllAction;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;

public class BattleFieldPanel extends DraggablePanel  {

	JPopupMenu menu = new JPopupMenu();
	private BufferedImage image;
	
	
	
	@Override
	public void removeAll()
	{
		super.removeAll();
	}
	
	
	public List<DisplayableCard> getCards()
	{
		List<DisplayableCard> selected = new ArrayList<DisplayableCard>();
		for(Component c : getComponents())
		{
			DisplayableCard card = (DisplayableCard)c;
			selected.add(card);
		}
		
		return selected;
	}
	
	
	
	public List<DisplayableCard> getSelectedCards()
	{
		List<DisplayableCard> selected = new ArrayList<DisplayableCard>();
			for(DisplayableCard card : getCards())
			{
				if(card.isSelected())
					selected.add(card);
			}
		
		return selected;
		
	}
	
	
	 public void paintComponent(Graphics g){
	        super.paintComponent(g);
	        if(image != null){
	            //g.drawImage(image, 0, 0, this);
	            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	        }
	    }
	
	public BattleFieldPanel() {
		
		super();
		setLayout(null);
		
		if(MTGControler.getInstance().get("/game/player-profil/background")!=null)
	        try {
	        	BufferedImage im = ImageIO.read(new File(MTGControler.getInstance().get("/game/player-profil/background")));
	        	setBackgroundPicture(im);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		
		
		menu.removeAll();
		menu.add(new JMenuItem(new UnselectAllAction()));
		menu.add(new JMenuItem(new SelectedTapActions()));
		menu.add(new JMenuItem(new FlipaCoinActions()));
		menu.add(new JMenuItem(new ChangeBackGroundActions()));
		setComponentPopupMenu(menu);
	}
	
	public void addComponent(DisplayableCard card)
	{
		this.add(card);
		card.setPosition(getOrigine());
	}

	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.BATTLEFIELD;
	}


	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {
		switch (to) {
			case GRAVEYARD:player.discardCardFromBattleField(mc.getMagicCard());break;
			case EXIL:player.exileCardFromBattleField(mc.getMagicCard());break;
			case HAND:player.returnCardFromBattleField(mc.getMagicCard());break;
			case LIBRARY:player.putCardInLibraryFromBattlefield(mc.getMagicCard(), true);
			default:break;
		}
		
	}


	@Override
	public void postTreatment(DisplayableCard c) {
		setComponentZOrder(c, 0);
	}



	public void setBackgroundPicture(BufferedImage im) {
		this.image=im;
		
	}

	
}
