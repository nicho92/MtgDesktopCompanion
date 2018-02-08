package org.magic.gui.models.conf;


import java.util.Map.Entry;

import org.magic.api.interfaces.MagicDAO;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class MagicDAOProvidersTableModel extends AbstractConfTreeTableModel<MagicDAO> 
{
    public MagicDAOProvidersTableModel() {
        super();
        listElements = MTGControler.getInstance().getDaoProviders();
        
    }
    
    @Override
    public void setValueAt(Object value, Object node, int column) {
    	
        String strValue = String.valueOf(value);
        
        if(node instanceof MagicDAO )
        {
        	selectedProvider=(MagicDAO)node;
        	if(column==2)
        	{
        		selectedProvider.enable(Boolean.parseBoolean(strValue));
        		MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
        		
        		for(MagicDAO dao : listElements)
        		{
        			if(dao!=selectedProvider)
        			{
        				dao.enable(false);
        				MTGControler.getInstance().setProperty(dao, dao.isEnable());
        	        	
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