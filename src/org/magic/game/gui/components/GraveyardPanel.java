package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;

public class GraveyardPanel extends DraggablePanel {
	
	public GraveyardPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
		setBackground(Color.GRAY);
		setPreferredSize(new Dimension(0, (int) (MTGControler.getInstance().getCardsDimension().getHeight()*30)));
	}
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.GRAVEYARD;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		if(i.isTapped())
			i.tap(false);
		add(i);
		i.setPosition(getOrigine());
	}


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		switch (to) {
			case BATTLEFIELD:player.returnCardFromGraveyard(mc);break;
			case EXIL:player.exileCardFromGraveyard(mc);break;
			case HAND:player.returnCardFromGraveyard(mc);break;
			case LIBRARY:player.putCardInLibraryFromGraveyard(mc, true);
			default:break;
		}
		
	}
	
	@Override
	public void postTreatment() {
		
		int NB=0;
		for(int i=getComponents().length-1;i>=0;i--)
		{
			DisplayableCard card = (DisplayableCard)getComponent(i);
			card.removeAllCounters();
			card.setBounds(5, 10+NB, card.getWidth(), card.getHeight());
			NB=NB+30;
		}
		
		getParent().getParent().revalidate();
		getParent().getParent().repaint();

//		for(int i=0;i<getComponents().length;i++)
//		{
//			DisplayableCard card = (DisplayableCard)getComponent(i);
//			card.setBounds(5, 10+NB, card.getWidth(), card.getHeight());
//			NB=NB+30;
//		}

		
	}
	
}
