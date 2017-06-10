package org.magic.game.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;
import org.magic.game.transfert.CardTransfertHandler;

public abstract class DraggablePanel extends JPanel implements Draggable{

  	Dimension d ;
	
	public JPopupMenu menu = new JPopupMenu();

	
    boolean dragging=true;
	protected Player player;

	public void executeDragging(DisplayableCard card,MouseEvent e){
		
	}
	
	public boolean isDragging() {
		return dragging;
	}

	public void enableDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public void setThumbnailSize(Dimension d)
	{
		this.d=d;
	}
	
	public int getCardWidth() {
		return (int)d.getWidth();
	}

	public int getCardHeight() {
		return (int)d.getHeight();
	}
 
	public DraggablePanel() {
		  setTransferHandler(new CardTransfertHandler());
		  setComponentPopupMenu(menu);
			
	}
	
	public List<DisplayableCard> lookupCardBy(String prop,String value)
	{
		List<DisplayableCard> ret = new ArrayList<DisplayableCard>();
		
			for(Component c : getComponents())
			{
				try {
					DisplayableCard card = (DisplayableCard)c;
					if(BeanUtils.describe(card.getMagicCard()).get(prop).equalsIgnoreCase(value))
						ret.add(card);
				} catch (Exception e) {
					
				}
			}
			return ret;
	}
	
  
	public abstract void moveCard(DisplayableCard mc, PositionEnum to);
	  
	public abstract void addComponent(DisplayableCard i);
	
	public abstract PositionEnum getOrigine();
	  
	public abstract void postTreatment(DisplayableCard c);
  
   public void updatePanel() {
	  revalidate();
	  repaint();
    	
    }  
  
  public void setPlayer(Player p)
  {
	  this.player=p;
  }
  
  public Player getPlayer() {
	return player;
}
 
}
