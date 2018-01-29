package org.magic.gui.models.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class MTGPicsCacheProviderTreeTableModel extends AbstractTreeTableModel {
	 private static final String[] COLUMN_NAMES = {"Provider","Value","Enabled"};
	    private MTGPicturesCache selectedProvider = null;
	    private List<MTGPicturesCache> lstMTGPicturesCache ;
	    Logger logger = MTGLogger.getLogger(this.getClass());
	    
	    
	    public MTGPicsCacheProviderTreeTableModel() {
	    	 super(new Object());
	        lstMTGPicturesCache = MTGControler.getInstance().getListCaches();
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
	        return  ((node instanceof Entry && column == 1)||(column==2));
	    }

	    @Override
	    public boolean isLeaf(Object node) {
	        return node instanceof Entry;
	    }

	    @Override
	    public int getChildCount(Object parent) {
	        if (parent instanceof MTGPicturesCache) {
	        	MTGPicturesCache dept = (MTGPicturesCache) parent;
	           return dept.getProperties().size();
	        }
	        return lstMTGPicturesCache.size();
	    }

	    @Override
	    public Object getChild(Object parent, int index) 
	    {
	    	  if (parent instanceof MTGPicturesCache) {
	        	MTGPicturesCache dept = (MTGPicturesCache) parent;
	            return getPropByIndex(dept,index);
	        }
	        return new ArrayList<MTGPicturesCache>(lstMTGPicturesCache).get(index);
	    }

	    private Entry<String,Object> getPropByIndex(MTGPicturesCache dept, int index)
	    {
	    	return (Map.Entry<String,Object>)dept.getProperties().entrySet().toArray()[index];
	    }
	    
	    
	    // This is not called in the JTree's default mode: use a native implementation.
	    @Override
	    public int getIndexOfChild(Object parent, Object child) {
	    	MTGPicturesCache dept = (MTGPicturesCache) parent;
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
	       if (node instanceof MTGPicturesCache) 
	       {
	    	   MTGPicturesCache prov = (MTGPicturesCache) node;
	            switch (column) {
	                case 0:return prov.getName();
	                case 2: return prov.isEnable();
	                default : return "";
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
	                default : return "";
	            }
	        }
	        return null;
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
	        		
	        		for(MTGPicturesCache daos : MTGControler.getInstance().getListCaches())
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
	    
	    
	    @Override
	    public Class<?> getColumnClass(int column) {
	    	if(column==2)
	    		return Boolean.class;
	    	
	    	return super.getColumnClass(column);
	    }



		public void setSelectedNode(MTGPicturesCache pathComponent) {
			selectedProvider=pathComponent;
		}
}
