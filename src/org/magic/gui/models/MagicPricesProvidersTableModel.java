package org.magic.gui.models;


import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MagicFactory;

public class MagicPricesProvidersTableModel extends AbstractTreeTableModel 
{
    private final static String[] COLUMN_NAMES = {"Provider","Value","Enabled"};
    private MagicPricesProvider selectedProvider = null;
    private Set<MagicPricesProvider> pricers = MagicFactory.getInstance().getSetPricers();
    static final Logger logger = LogManager.getLogger(MagicPricesProvidersTableModel.class.getName());

    
    
    public MagicPricesProvidersTableModel() {
        super(new Object());
        
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public boolean isCellEditable(Object node, int column) {
        if (node instanceof Entry && column == 1) {
            return true;
        }
        if(column==2)
        	return true;
        
        return false;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Entry;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof MagicPricesProvider) {
        	MagicPricesProvider dept = (MagicPricesProvider) parent;
            return dept.getProperties().size();
        }
        return pricers.size();
    }

    @Override
    public Object getChild(Object parent, int index) 
    {
    	  if (parent instanceof MagicPricesProvider) {
        	MagicPricesProvider dept = (MagicPricesProvider) parent;
            return getPropByIndex(dept,index);
        }
        return new ArrayList(pricers).get(index);
    }

    private Entry<String,Object> getPropByIndex(MagicPricesProvider dept, int index)
    {
    	return (Map.Entry<String,Object>)dept.getProperties().entrySet().toArray()[index];
    }
    
    
    // This is not called in the JTree's default mode: use a native implementation.
    @Override
    public int getIndexOfChild(Object parent, Object child) {
    	MagicPricesProvider dept = (MagicPricesProvider) parent;
        Entry k = (Entry) child;
        return getPosition(k,dept.getProperties());
    }
    
    private int getPosition(Entry k, Properties p)
    {
    	for(int i=0;i<p.keySet().size();i++)
    	{
    		if(p.keySet().toArray()[i].toString().equals(k.getKey()))
    			return i;
    	}
    	return -1;
    }

    @Override
    public Object getValueAt(Object node, int column) {
       if (node instanceof MagicPricesProvider) 
       {
    	   MagicPricesProvider prov = (MagicPricesProvider) node;
            switch (column) {
                case 0:return prov.getName();
                case 2: return prov.isEnable();
            }
        } 
        else if (node instanceof Entry) 
        {
        	Entry emp = (Entry) node;
        	  switch (column) {
                case 0:
                    return emp.getKey();
                case 1:
                    return emp.getValue();
            }
        }
        return null;
    }

    
    
    public void setValueAt(Object value, Object node, int column) {
    	
        String strValue = String.valueOf(value);
        
        if(node instanceof MagicPricesProvider )
        {
        	selectedProvider=(MagicPricesProvider)node;
        	if(column==2)
        	{
        		selectedProvider.enable(Boolean.parseBoolean(strValue));
        		MagicFactory.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
        	}
        }
        if(node instanceof Entry )
	        if(column==1)
	    	{
	        	String k = (String)((Entry)node).getKey();
	        	selectedProvider.setProperties(k, strValue);
	        	logger.debug("put " + k+"="+strValue + " to " + selectedProvider);
	        	selectedProvider.save();
	    	}    
   }
    
    
    @Override
    public Class<?> getColumnClass(int column) {
    	if(column==2)
    		return Boolean.class;
    	
    	return super.getColumnClass(column);
    }



	public void setSelectedNode(MagicPricesProvider pathComponent) {
		selectedProvider=pathComponent;
	}
    
}