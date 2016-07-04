package org.magic.gui.game.transfert;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.DisplayableCard;

public class TransferableCard implements Transferable {
	 
    protected static DataFlavor displayableCardFlavor = new DataFlavor(DisplayableCard.class, "Label item");
    
    
    protected static DataFlavor[] supportedFlavors = {
    	displayableCardFlavor,
        DataFlavor.stringFlavor,
    };

    DisplayableCard itemLab;
    
    public TransferableCard(DisplayableCard i) { this.itemLab = i; }

    public DataFlavor[] getTransferDataFlavors() { return supportedFlavors; }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
    
    	if (flavor.equals(displayableCardFlavor) || flavor.equals(DataFlavor.stringFlavor)) 
    		return true;
    
    return false;
  }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
    {
      if (flavor.equals(DataFlavor.stringFlavor)) 
          return itemLab.toString();
      else if(flavor.equals(displayableCardFlavor))
    	  return itemLab;
      else
          throw new UnsupportedFlavorException(flavor);
    }
 }