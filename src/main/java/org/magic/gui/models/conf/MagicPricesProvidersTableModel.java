package org.magic.gui.models.conf;


import java.util.Map.Entry;

import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.gui.abstracts.AbstractConfTreeTableModel;
import org.magic.services.MTGControler;

public class MagicPricesProvidersTableModel extends AbstractConfTreeTableModel<MagicPricesProvider>
{
   
    
    public MagicPricesProvidersTableModel() {
        super();
        listElements= MTGControler.getInstance().getPricers();
    }
    
    @Override
    public void setValueAt(Object value, Object node, int column) {
    	
        String strValue = String.valueOf(value);
        
        if(node instanceof MagicPricesProvider )
        {
        	selectedProvider=(MagicPricesProvider)node;
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