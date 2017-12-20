package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.LineBorder;

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
		setComponentZOrder(i, 0);
		for(int y=getComponentCount()-1;y==1;y--)
		{
			setComponentZOrder(getComponent(y), y);
		}
		
		i.removeAllCounters();
	}


	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {
		switch (to) {
			case BATTLEFIELD:player.playCardFromGraveyard(mc.getMagicCard());break;
			case EXIL:player.exileCardFromGraveyard(mc.getMagicCard());break;
			case HAND:player.returnCardFromGraveyard(mc.getMagicCard());break;
			case LIBRARY:player.putCardInLibraryFromGraveyard(mc.getMagicCard(), true);
			default:break;
		}
		
	}
	
	@Override
	public void postTreatment(DisplayableCard c) {
		
		int NB=0;
		
		for(int i=getComponents().length-1;i>=0;i--)
		{
			DisplayableCard card = (DisplayableCard)getComponent(i);
			card.setBounds(5, 10+NB, card.getWidth(), card.getHeight());
			NB=NB+30;
		}
		getParent().getParent().revalidate();
		getParent().getParent().repaint();
	}
	@Override
	public String toString() {
		return "GraveyardPanel";
	}
	
}
