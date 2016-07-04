package org.magic.gui.game;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import org.magic.services.games.PositionEnum;

public class BattleFieldPanel extends DragDestinationPanel implements MouseListener{

	private List<DisplayableCard> stack;
	private DisplayableCard selectedCard;
	
	
	public BattleFieldPanel() {
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		stack=new ArrayList<DisplayableCard>();
		this.addMouseListener(this);
	}
	
	
	public void addCard(DisplayableCard card)
	{
		card.setOrigine(PositionEnum.BATTLEFIELD);
		stack.add(card);
		this.add(card);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(e.getPoint());
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.BATTLEFIELD;
	}

	
	


	
}
