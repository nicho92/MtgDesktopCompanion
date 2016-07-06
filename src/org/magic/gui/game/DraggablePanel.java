package org.magic.gui.game;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.magic.api.beans.MagicCard;
import org.magic.game.PositionEnum;
import org.magic.gui.game.transfert.CardTransfertHandler;

public abstract class DraggablePanel extends JPanel {

  	int width=112;
	int height=155;
	
    boolean dragging=true;

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
	  /*addMouseListener(new MouseAdapter() {
		  public void mousePressed(MouseEvent me) {
			  DraggablePanel p = (DraggablePanel) me.getSource();
			  Component c = SwingUtilities.getDeepestComponentAt(p, me.getX(), me.getY());
			  if (c != null && c instanceof DisplayableCard) {
				  
				  if(dragging)
					  p.getTransferHandler().exportAsDrag((DisplayableCard)c, me, TransferHandler.MOVE);
			  }
			}
	  });*/
  }
  
  public abstract void moveCard(MagicCard mc, PositionEnum to);

  
  
  public abstract void addComponent(DisplayableCard i);
	
  public abstract PositionEnum getOrigine();
 
}
