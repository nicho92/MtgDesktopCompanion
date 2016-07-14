package org.magic.gui.game;

import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.api.pictures.impl.GathererPicturesProvider;
import org.magic.game.Player;
import org.magic.game.PositionEnum;
import org.magic.gui.game.transfert.CardTransfertHandler;

public abstract class DraggablePanel  extends JPanel implements Draggable{

  	int width=112;
	int height=155;
	
	protected GathererPicturesProvider gatherer;
	
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
	  gatherer = new GathererPicturesProvider();
	  setTransferHandler(new CardTransfertHandler());
  }
  
  /* (non-Javadoc)
 * @see org.magic.gui.game.Draggable#moveCard(org.magic.api.beans.MagicCard, org.magic.game.PositionEnum)
 */
@Override
public abstract void moveCard(MagicCard mc, PositionEnum to);
  
  /* (non-Javadoc)
 * @see org.magic.gui.game.Draggable#addComponent(org.magic.gui.game.DisplayableCard)
 */
@Override
public abstract void addComponent(DisplayableCard i);
	
  /* (non-Javadoc)
 * @see org.magic.gui.game.Draggable#getOrigine()
 */
@Override
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
