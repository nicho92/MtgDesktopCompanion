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
		System.out.println(e);
		 DisplayableCard card =  ((DisplayableCard)e.getComponent());
		 if(!SwingUtilities.isRightMouseButton(e))
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
	
	public void mousePressed(MouseEvent me) {
		 DisplayableCard card =  ((DisplayableCard)me.getComponent());
		 if(!SwingUtilities.isRightMouseButton(me))
		 {
			  if(card.isDraggable())
				  ((DraggablePanel)card.getParent()).getTransferHandler().exportAsDrag(card, me, TransferHandler.MOVE);
		 }
	  	 
	 }
}
