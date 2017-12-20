package org.magic.gui.models.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class PluginTableModel <T extends MTGPlugin> extends AbstractTreeTableModel {
	 private final static String[] COLUMN_NAMES = {"Provider","Value","Enabled"};
	    private MTGPlugin selectedPlugin = null;
	    private List<MTGPlugin> lstPlugins ;
	    Logger logger = MTGLogger.getLogger(this.getClass());
	    
	    
	    public PluginTableModel(List<MTGPlugin> t) {
	    	 super(new Object());
	    	 lstPlugins = t;
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
	        if (parent instanceof MTGPlugin) {
	        	MTGPlugin dept = (MTGPlugin) parent;
	           return dept.getProperties().size();
	        }
	        return lstPlugins.size();
	    }

	    @Override
	    public Object getChild(Object parent, int index) 
	    {
	    	  if (parent instanceof MTGPlugin) {
	    		  MTGPlugin dept = (MTGPlugin) parent;
	            return getPropByIndex(dept,index);
	        }
	        return new ArrayList<MTGPlugin>(lstPlugins).get(index);
	    }

	    private Entry<String,Object> getPropByIndex(MTGPlugin dept, int index)
	    {
	    	return (Map.Entry<String,Object>)dept.getProperties().entrySet().toArray()[index];
	    }
	    
	    
	    // This is not called in the JTree's default mode: use a native implementation.
	    @Override
	    public int getIndexOfChild(Object parent, Object child) {
	    	T dept = (T) parent;
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
	       if (node instanceof MTGPlugin) 
	       {
	    	   T prov = (T) node;
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
	        
	        if(node instanceof MTGPlugin )
	        {
	        	selectedPlugin=(T)node;
	        	if(column==2)
	        	{
	        		selectedPlugin.enable(Boolean.parseBoolean(strValue));
	        		MTGControler.getInstance().setProperty(selectedPlugin, selectedPlugin.isEnable());
	        		
	        		for(MTGPlugin daos : lstPlugins)
	        		{
	        			if(daos!=selectedPlugin)
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
		        	selectedPlugin.setProperties(k, strValue);
		        	logger.debug("put " + k+"="+strValue + " to " + selectedPlugin);
		        	selectedPlugin.save();
		    	}    
	   }
	    
	    
	    @Override
	    public Class<?> getColumnClass(int column) {
	    	if(column==2)
	    		return Boolean.class;
	    	
	    	return super.getColumnClass(column);
	    }



		public void setSelectedNode(MTGPlugin pathComponent) {
			selectedPlugin=pathComponent;
		}
}
