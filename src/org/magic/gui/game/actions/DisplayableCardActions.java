package org.magic.gui.game.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import org.magic.game.Player;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.DraggablePanel;

public class DisplayableCardActions extends MouseAdapter {

	
	private Player p;
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println(e);
	}
	
	
	
	public DisplayableCardActions(Player player) {
		this.p=player;
	}



	public void mouseEntered(MouseEvent me) {
		((DisplayableCard)me.getComponent()).getPopUp().show(me.getComponent(),me.getX(),me.getY());
		((DisplayableCard)me.getComponent()).setVisible(true);
	}
	
	
	
	@Override
	public void mouseExited(MouseEvent me) {
		((DisplayableCard)me.getComponent()).setVisible(false);

	}
	
	
	 public void mousePressed(MouseEvent me) {
		 
		 DisplayableCard card =  ((DisplayableCard)me.getComponent());
		 
		 if(SwingUtilities.isRightMouseButton(me))
		 {
			 
			 if(card.isTapped())
			 {
				 card.tap(false);
				 p.logAction("Untap " + card.getMagicCard() );
			 }
			 else
			 {
				 card.tap(true);
				 p.logAction("Tap " + card.getMagicCard() + " (" + card.getMagicCard().getText()+")");
			 }
		 }
		 else
		 {
				  if(card.isSelected())
				  {	  
					  card.setBorder(null);
				  	  card.setSelected(false);
				  }
				  else
				  {
					  card.setBorder(new LineBorder(Color.RED));
				  	  card.setSelected(true);
				  }
				  if(card.isDraggable())
					  ((DraggablePanel)card.getParent()).getTransferHandler().exportAsDrag(card, me, TransferHandler.MOVE);
			}
		  	 
		 }
}
