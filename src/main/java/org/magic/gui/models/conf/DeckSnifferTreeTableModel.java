package org.magic.gui.models.conf;

import java.util.Map.Entry;

import org.magic.api.interfaces.DeckSniffer;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class DeckSnifferTreeTableModel extends AbstractConfTreeTableModel<DeckSniffer> {
	    public DeckSnifferTreeTableModel() {
	        super();
	        listElements =MTGControler.getInstance().getDeckSniffers();
	    }
	
	    @Override
	    public void setValueAt(Object value, Object node, int column) {
	    	
	        String strValue = String.valueOf(value);
	        
	        if(node instanceof DeckSniffer )
	        {
	        	selectedProvider=(DeckSniffer)node;
	        	if(column==2)
	        	{
	        		selectedProvider.enable(Boolean.parseBoolean(strValue));
	        		MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
	        		
	        		
	        	}
	        }
	        if(node instanceof Entry )
		        if(column==1)
		    	{
		        	String k = (String)((Entry)node).getKey();
		        	selectedProvider.setProperties(k, strValue);
		        	logger.debug("put " + k+"="+strValue + " to " + selectedProvider);
		        	((Entry)node).setValue(strValue);
		        	selectedProvider.save();
		    	}    
	   }
}
