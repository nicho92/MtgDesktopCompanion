package org.magic.gui.game.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.PositionEnum;
import org.magic.gui.game.actions.battlefield.UntapAllAction;
import org.magic.gui.game.actions.cards.TapActions;
import org.magic.gui.game.actions.cards.TransferActions;

public class BattleFieldPanel extends DraggablePanel  {

	private List<DisplayableCard> stack;
	JPopupMenu menu = new JPopupMenu();
	
	
	
	public List<DisplayableCard> getCards()
	{
		return stack;
	}
	
	
	
	public BattleFieldPanel() {
		
		super();
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		stack=new ArrayList<DisplayableCard>();
		
		menu.removeAll();
		menu.add(new JMenuItem(new UntapAllAction(this)));
		setComponentPopupMenu(menu);
	}
	
	
	public void addComponent(DisplayableCard card)
	{
		stack.add(card);
		this.add(card);
	}

	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.BATTLEFIELD;
	}


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		switch (to) {
			case GRAVEYARD:player.discardCardFromBattleField(mc);break;
			case EXIL:player.exileCardFromBattleField(mc);break;
			case HAND:player.returnCardFromBattleField(mc);break;
			case LIBRARY:player.putCardInLibraryFromBattlefield(mc, true);
			default:break;
		}
		
	}


	@Override
	public void postTreatment() {
		// TODO Auto-generated method stub
		
	}

	
}
