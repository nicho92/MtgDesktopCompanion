package org.magic.gui.models.conf;


import java.util.Map.Entry;

import org.magic.api.interfaces.PictureProvider;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class PicturesProvidersTableModel extends AbstractConfTreeTableModel<PictureProvider> 
{
	    public PicturesProvidersTableModel() {
	    	  super();
	    	  listElements = MTGControler.getInstance().getPicturesProviders();
	    }
	    
	    @Override
	    public void setValueAt(Object value, Object node, int column) {
	    	
	        String strValue = String.valueOf(value);
	        
	        if(node instanceof PictureProvider )
	        {
	        	selectedProvider=(PictureProvider)node;
	        	if(column==2)
	        	{
	        		selectedProvider.enable(Boolean.parseBoolean(strValue));
	        		MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
	        		
	        		for(PictureProvider pprovider : listElements)
	        		{
	        			if(pprovider!=selectedProvider)
	        			{
	        				pprovider.enable(false);
	        				MTGControler.getInstance().setProperty(pprovider, pprovider.isEnable());
	        	        	
	        			}
	        		}
	        		
	        	}
	        }
	        if(node instanceof Entry )
		        if(column==1)
		    	{
		        	String k = (String)((Entry)node).getKey();
		        	selectedProvider.getProperties().put(k, strValue);
		        	logger.debug("put " + k+"="+strValue + " to " + selectedProvider);
		        	((Entry)node).setValue(strValue);
		        	selectedProvider.save();
		    	}    
	   }
	
    
}