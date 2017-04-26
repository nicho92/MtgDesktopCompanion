package org.magic.game.gui.components;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;
import org.magic.game.transfert.CardTransfertHandler;

public abstract class DraggablePanel  extends JPanel implements Draggable{

  	int width=112;
	int height=155;
	
	public JPopupMenu menu = new JPopupMenu();

	
    boolean dragging=true;
	protected Player player;

	public boolean isDragging() {
		return dragging;
	}

	public void enableDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public void setThumbnailSize(int w,int h)
	{
		this.width=w;
		this.height=h;
	}
	
	public int getCardWidth() {
		return width;
	}

	public void setCardWidth(int width) {
		this.width = width;
	}

	public int getCardHeight() {
		return height;
	}

	public void setCardHeight(int height) {
		this.height = height;
	}

 
	public DraggablePanel() {
		  setTransferHandler(new CardTransfertHandler());
		  setComponentPopupMenu(menu);
			
	}
  
	public abstract void moveCard(MagicCard mc, PositionEnum to);
	  
	public abstract void addComponent(DisplayableCard i);
	
	public abstract PositionEnum getOrigine();
	  
  public abstract void postTreatment();
  
  public void setPlayer(Player p)
  {
	  this.player=p;
  }
  
  public Player getPlayer() {
	return player;
}
 
}
