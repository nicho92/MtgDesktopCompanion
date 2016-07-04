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
import org.magic.gui.game.DragDestinationPanel;

public class MagicCardTargetAdapter extends DropTargetAdapter {

    private DropTarget dropTarget;
    
    
    private DragDestinationPanel destination;
    private DragDestinationPanel source;
     
    DisplayableCard dc;
    public MagicCardTargetAdapter(DragDestinationPanel source, DragDestinationPanel dest) {
    	destination = dest;
    	this.source=source;
    	dropTarget = new DropTarget(destination, DnDConstants.ACTION_MOVE, this, true, null);
    }

    
    
    
    public void reject()
    {
    	destination.setCursor(DragSource.DefaultCopyNoDrop);
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {
	    destination.setCursor(Cursor.getDefaultCursor());
    }
    
    public void dragOver(DropTargetDragEvent event) {
    	 Transferable tr = event.getTransferable();
    	 try 
    	 {
			
    		dc = (DisplayableCard)tr.getTransferData(TransferableCard.displayableCardFlavor);
			
			
			
			dc.setLocation(event.getLocation());
			destination.repaint();
    	 }
    	 catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
    	 }
    }
    
  public void drop(DropTargetDropEvent event) {
    try {

      Transferable tr = event.getTransferable();
      dc = (DisplayableCard) tr.getTransferData(TransferableCard.displayableCardFlavor);

        if (event.isDataFlavorSupported(TransferableCard.displayableCardFlavor)) {

          event.acceptDrop(DnDConstants.ACTION_MOVE);
          	dc.setBounds((int)event.getLocation().getX(),(int)event.getLocation().getY(),dc.getWidth(),dc.getHeight());
         	if(dc.getOrigine()!=destination.getOrigine())
          	{
          		dc.setOrigine(destination.getOrigine());	
          		destination.add(dc);
          	}

         	
          	
	        destination.revalidate();
	        destination.repaint();
	         
	        
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
