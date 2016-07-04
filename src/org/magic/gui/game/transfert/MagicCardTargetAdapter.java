package org.magic.gui.game.transfert;

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.io.IOException;

import javax.swing.JPanel;

import org.magic.gui.game.DisplayableCard;
import org.magic.services.games.Player;

public class MagicCardTargetAdapter extends DropTargetAdapter {

    private DropTarget dropTarget;
    private JPanel panel;
     
    public MagicCardTargetAdapter(JPanel il) {
    	panel = il;
    	dropTarget = new DropTarget(il, DnDConstants.ACTION_MOVE, this, true, null);
    }

    
    
    
    public void reject()
    {
    	panel.setCursor(DragSource.DefaultCopyNoDrop);
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {
	    panel.setCursor(Cursor.getDefaultCursor());
    }
    
    public void dragOver(DropTargetDragEvent event) {
    	
    	 Transferable tr = event.getTransferable();
    	 try 
    	 {
			DisplayableCard i = (DisplayableCard)tr.getTransferData(TransferableCard.displayableCardFlavor);
			i.setBounds((int)event.getLocation().getX(),(int)event.getLocation().getY(),i.getWidth(),i.getHeight());
		}
		catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
    }
    
  public void drop(DropTargetDropEvent event) {
    try {

      Transferable tr = event.getTransferable();
      DisplayableCard i = (DisplayableCard) tr.getTransferData(TransferableCard.displayableCardFlavor);

        if (event.isDataFlavorSupported(TransferableCard.displayableCardFlavor)) {

          event.acceptDrop(DnDConstants.ACTION_MOVE);
          	i.setBounds((int)event.getLocation().getX(),(int)event.getLocation().getY(),i.getWidth(),i.getHeight());
          	
            panel.add(i);
	        panel.revalidate();
	        panel.repaint();
	         
	        
          event.dropComplete(true);
          return;
        }
        
        
      event.rejectDrop();
      
      
    } catch (Exception e) {
      e.printStackTrace();
      event.rejectDrop();
    }
  }
}
