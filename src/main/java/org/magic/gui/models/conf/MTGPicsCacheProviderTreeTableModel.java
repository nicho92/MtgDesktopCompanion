package org.magic.gui.models.conf;

import java.util.Map.Entry;

import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class MTGPicsCacheProviderTreeTableModel extends AbstractConfTreeTableModel<MTGPicturesCache> {
	
	    public MTGPicsCacheProviderTreeTableModel() {
	    	super();
	        listElements= MTGControler.getInstance().getListCaches();
	    }

	    @Override
	    public void setValueAt(Object value, Object node, int column) {
	    	
	        String strValue = String.valueOf(value);
	        
	        if(node instanceof MTGPicturesCache )
	        {
	        	selectedProvider=(MTGPicturesCache)node;
	        	if(column==2)
	        	{
	        		selectedProvider.enable(Boolean.parseBoolean(strValue));
	        		MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
	        		
	        		for(MTGPicturesCache daos : listElements)
	        		{
	        			if(daos!=selectedProvider)
	        			{
	        				daos.enable(false);
	        				MTGControler.getInstance().setProperty(daos, daos.isEnable());
	        	        	
	        			}
	        		}
	        		
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
