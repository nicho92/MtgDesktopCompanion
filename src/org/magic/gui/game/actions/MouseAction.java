package org.magic.gui.game.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import org.magic.game.Player;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.DraggablePanel;

public class MouseAction extends MouseAdapter {

	
	private Player p;
	
	
	public MouseAction(Player player) {
		this.p=player;
	}



	public void mouseEntered(MouseEvent me) {
	}
	
	
	
	@Override
	public void mouseExited(MouseEvent me) {
		// ((DisplayableCard)me.getComponent()).setBorder(null);

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
