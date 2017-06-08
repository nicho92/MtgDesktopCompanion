package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

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
  
	public abstract void moveCard(DisplayableCard mc, PositionEnum to);
	  
	public abstract void addComponent(DisplayableCard i);
	
	public abstract PositionEnum getOrigine();
	  
	public abstract void postTreatment();
  
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
