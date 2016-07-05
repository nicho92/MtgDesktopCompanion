package org.magic.gui.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.magic.services.games.PositionEnum;

public class BattleFieldPanel extends DraggablePanel  {

	private List<DisplayableCard> stack;
	private DisplayableCard selectedCard;
	
	
	public BattleFieldPanel() {
		
		super();
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		stack=new ArrayList<DisplayableCard>();
		
	}
	
	
	public void addComponent(DisplayableCard card)
	{
		card.setOrigine(PositionEnum.BATTLEFIELD);
		stack.add(card);
		this.add(card);
	}

	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.BATTLEFIELD;
	}

	
}
