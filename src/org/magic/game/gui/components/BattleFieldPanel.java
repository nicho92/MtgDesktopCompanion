package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.game.actions.battlefield.ChangeBackGroundAction;
import org.magic.game.actions.battlefield.SelectedTapActions;
import org.magic.game.model.PositionEnum;

public class BattleFieldPanel extends DraggablePanel  {

	private List<DisplayableCard> stack;
	JPopupMenu menu = new JPopupMenu();
	private BufferedImage image;
	
	@Override
	public void removeAll()
	{
		super.removeAll();
		stack.clear();
	}
	
	
	public List<DisplayableCard> getCards()
	{
		return stack;
	}
	
	
	public List<DisplayableCard> getSelectedCards()
	{
		List<DisplayableCard> selected = new ArrayList<DisplayableCard>();
			for(DisplayableCard c : stack)
				if(c.isSelected())
					selected.add(c);
		
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
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		stack=new ArrayList<DisplayableCard>();
		
		menu.removeAll();
		menu.add(new JMenuItem(new SelectedTapActions(this)));
		menu.add(new JMenuItem(new ChangeBackGroundAction(this)));
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



	public void setBackgroundPicture(BufferedImage im) {
		this.image=im;
		
	}

	
}
