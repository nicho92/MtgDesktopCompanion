package org.magic.gui.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.magic.gui.game.transfert.MagicCardTargetAdapter;
import org.magic.services.games.PositionEnum;

public class BattleFieldPanel extends JPanel {

	private List<DisplayableCard> stack;
	private DisplayableCard selectedCard;
	
	
	public BattleFieldPanel() {
		setBackground(Color.DARK_GRAY);
		new MagicCardTargetAdapter(this);
		setLayout(null);
		stack=new ArrayList<DisplayableCard>();
	}
	
	
	public void addCard(DisplayableCard card)
	{
		card.setOrigine(PositionEnum.BATTLEFIELD);
		stack.add(card);
		this.add(card);
	}



	
}
