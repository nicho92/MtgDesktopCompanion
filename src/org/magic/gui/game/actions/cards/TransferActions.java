package org.magic.gui.game.actions.cards;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.magic.gui.game.components.DisplayableCard;
import org.magic.gui.game.components.DraggablePanel;
import org.magic.gui.game.components.GamePanelGUI;

public class TransferActions extends MouseAdapter {


	public void mouseEntered(MouseEvent me) {
		GamePanelGUI.getInstance().describeCard(((DisplayableCard)me.getComponent()));
	}


	public void mousePressed(MouseEvent e) {
		DisplayableCard card =  ((DisplayableCard)e.getComponent());
			 
		
			if(SwingUtilities.isLeftMouseButton(e)){
			if(card.isDraggable())
			  ((DraggablePanel)card.getParent()).getTransferHandler().exportAsDrag(card, e, TransferHandler.MOVE); //block click event
			 
			 
			 
			 if(e.getClickCount()==2)
				  if(card.isTappable())
					if(card.isTapped())
					{
						card.tap(false);
						GamePanelGUI.getInstance().getPlayer().logAction("Untap " + card.getMagicCard());
					}
					else
					{
						card.tap(true);
						GamePanelGUI.getInstance().getPlayer().logAction("Tap " + card.getMagicCard());
					}
			 
			}
		
	  	 
	 }
}
