package org.magic.gui.game.actions;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.DraggablePanel;
import org.magic.gui.game.GamePanelGUI;

public class SelectionActions extends MouseAdapter {


	public void mouseEntered(MouseEvent me) {
		GamePanelGUI.getInstance().describeCard(((DisplayableCard)me.getComponent()));
	}

	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		 DisplayableCard card =  ((DisplayableCard)e.getComponent());
		 if(SwingUtilities.isLeftMouseButton(e))
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
			 
			
		 }
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
