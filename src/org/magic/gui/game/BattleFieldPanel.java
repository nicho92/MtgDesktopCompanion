package org.magic.gui.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.game.GameManager;
import org.magic.game.PositionEnum;
import org.magic.gui.game.actions.MouseAction;

public class BattleFieldPanel extends DraggablePanel  {

	private List<DisplayableCard> stack;
	
	
	public BattleFieldPanel() {
		
		super();
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		stack=new ArrayList<DisplayableCard>();
		
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
