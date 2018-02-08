package org.magic.gui.models.conf;

import java.util.Map.Entry;

import org.magic.api.interfaces.DashBoard;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class DashBoardProviderTreeTableModel extends AbstractConfTreeTableModel<DashBoard> {
	   
	    public DashBoardProviderTreeTableModel() {
	    	super();
	        listElements = MTGControler.getInstance().getDashBoards();
	    }
	    
	    @Override
	    public void setValueAt(Object value, Object node, int column) {
	    	
	        String strValue = String.valueOf(value);
	        
	        if(node instanceof DashBoard )
	        {
	        	selectedProvider=(DashBoard)node;
	        	if(column==2)
	        	{
	        		selectedProvider.enable(Boolean.parseBoolean(strValue));
	        		MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
	        		
	        		for(DashBoard daos : listElements)
	        		{
	        			if(daos!=selectedProvider)
	        			{
	        				daos.enable(false);
	        				MTGControler.getInstance().setProperty(daos, daos.isEnable());
	        	        	
	        			}
	        		}
	        		
	        	}
	        }
	        if(node instanceof Entry && (column==1) )
		    	{
		        	String k = (String)((Entry)node).getKey();
		        	selectedProvider.setProperties(k, strValue);
		        	logger.debug("put " + k+"="+strValue + " to " + selectedProvider);
		        	((Entry)node).setValue(strValue);
		        	selectedProvider.save();
		    	}    
	   }
	    
	  
}
