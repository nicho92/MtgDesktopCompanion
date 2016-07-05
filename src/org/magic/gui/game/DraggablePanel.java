package org.magic.gui.game;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.magic.gui.game.transfert.CardTransfertHandler;
import org.magic.services.games.PositionEnum;

public abstract class DraggablePanel extends JPanel implements MouseListener{

  protected CardTransfertHandler dndHandler = new CardTransfertHandler();	

 
	public DraggablePanel() {
	  setTransferHandler(dndHandler);
	  addMouseListener(this);

  }
  
  @Override
	public void mousePressed(MouseEvent me) {
	  DraggablePanel p = (DraggablePanel) me.getSource();
	  Component c = SwingUtilities.getDeepestComponentAt(this, me.getX(), me.getY());
	  if (c != null && c instanceof DisplayableCard) {
		p.getTransferHandler().exportAsDrag((DisplayableCard)c, me, TransferHandler.MOVE);
	  }
	}
	
  @Override
  public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}
  
  @Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
  
  @Override
	public void mouseReleased(MouseEvent e) {
	
		
	}
  

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
  
  
  public abstract void addComponent(DisplayableCard i);
	
  public abstract PositionEnum getOrigine();
 
}
