package org.magic.gui.models.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ServersTreeTableModel extends AbstractTreeTableModel {
	
	   	private MTGServer selectedProvider = null;
	    private List<MTGServer> exports =MTGControler.getInstance().getServers();
	    private Logger logger = MTGLogger.getLogger(this.getClass());
	    private static final String[] COLUMN_NAMES = {"Deck Website","Value","Enabled"};
		

	    
	    public ServersTreeTableModel() {
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
	        return  ((node instanceof Entry && column == 1)||(column==2));
	    }
	    
	    @Override
	    public boolean isLeaf(Object node) {
	        return node instanceof Entry;
	    }

	    @Override
	    public int getChildCount(Object parent) {
	        if (parent instanceof MTGServer) {
	        	MTGServer dept = (MTGServer) parent;
	            return dept.getProperties().size();
	        }
	        return exports.size();
	    }

	    @Override
	    public Object getChild(Object parent, int index) 
	    {
	    	  if (parent instanceof MTGServer) {
	        	MTGServer dept = (MTGServer) parent;
	            return getPropByIndex(dept,index);
	        }
	        return new ArrayList<MTGServer>(exports).get(index);
	    }

	    private Entry<String,Object> getPropByIndex(MTGServer dept, int index)
	    {
	    	return (Map.Entry<String,Object>)dept.getProperties().entrySet().toArray()[index];
	    }
	    
	    
	    // This is not called in the JTree's default mode: use a native implementation.
	    @Override
	    public int getIndexOfChild(Object parent, Object child) {
	    	MTGServer dept = (MTGServer) parent;
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
	       if (node instanceof MTGServer) 
	       {
	    	   MTGServer prov = (MTGServer) node;
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
	    
	    
	    @Override
	    public Class<?> getColumnClass(int column) {
	    	if(column==2)
	    		return Boolean.class;
	    	
	    	return super.getColumnClass(column);
	    }



		public void setSelectedNode(MTGServer pathComponent) {
			selectedProvider=pathComponent;
		}
}
