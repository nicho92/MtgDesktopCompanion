package org.magic.gui.models.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class ServersTreeTableModel extends AbstractConfTreeTableModel<MTGServer> {
		
		public ServersTreeTableModel() {
	        super();
	        listElements=MTGControler.getInstance().getServers();
	    }
	    
	    @Override
	    public void setValueAt(Object value, Object node, int column) {
	    	
	        String strValue = String.valueOf(value);
	        
	        if(node instanceof MTGServer )
	        {
	        	selectedProvider=(MTGServer)node;
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
