package org.magic.gui.game;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.transfert.CardTransfertHandler;
import org.magic.services.games.PositionEnum;

public abstract class DraggablePanel extends JPanel implements MouseListener{

  protected CardTransfertHandler dndHandler = new CardTransfertHandler();	

  	int width=112;
	int height=155;
    boolean dragging;

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
	  setTransferHandler(dndHandler);
	  addMouseListener(this);

  }
  
  public abstract void moveCard(MagicCard mc, PositionEnum to);

  
  
  @Override
	public void mousePressed(MouseEvent me) {
	  DraggablePanel p = (DraggablePanel) me.getSource();
	  Component c = SwingUtilities.getDeepestComponentAt(this, me.getX(), me.getY());
	  if (c != null && c instanceof DisplayableCard) {
		p.getTransferHandler().exportAsDrag((DisplayableCard)c, me, TransferHandler.MOVE);
	  }
	}
	
  @Override
  public void mouseClicked(MouseEvent me) {
	  Component c = SwingUtilities.getDeepestComponentAt(this, me.getX(), me.getY());
	  System.out.println(c);
	  if (c != null && c instanceof DisplayableCard) {
		((DisplayableCard)c).tap(true);
	  }
	}
  
  @Override
	public void mouseExited(MouseEvent e) {
	  
		
	}
  
  @Override
	public void mouseReleased(MouseEvent e) {
	  
		
	}
  

	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}
  
  
  public abstract void addComponent(DisplayableCard i);
	
  public abstract PositionEnum getOrigine();
 
}
