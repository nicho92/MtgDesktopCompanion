package org.magic.game.gui.components;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;
import org.magic.game.transfert.CardTransfertHandler;

public abstract class DraggablePanel extends JPanel implements Draggable{

  	Dimension d ;
	
	public JPopupMenu menu = new JPopupMenu();

	
    boolean dragging=true;
	protected Player player;

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
  
	public abstract void moveCard(MagicCard mc, PositionEnum to);
	  
	public abstract void addComponent(DisplayableCard i);
	
	public abstract PositionEnum getOrigine();
	  
  public abstract void postTreatment();
  
   public void update() {
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
