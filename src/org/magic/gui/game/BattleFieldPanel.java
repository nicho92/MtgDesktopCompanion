package org.magic.gui.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.game.GameManager;
import org.magic.game.PositionEnum;

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
			case GRAVEYARD:GameManager.getInstance().getPlayer().discardCardFromBattleField(mc);break;
			case EXIL:GameManager.getInstance().getPlayer().exileCardFromBattleField(mc);break;
			case HAND:GameManager.getInstance().getPlayer().returnCardFromBattleField(mc);break;
			default:break;
		}
		
	}

	
}
