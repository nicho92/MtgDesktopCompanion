package org.magic.gui.models.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.CardExporter;
import org.magic.services.MTGDesktopCompanionControler;

public class ExportsTreeTableModel extends AbstractTreeTableModel {
	
	   	private CardExporter selectedProvider = null;
	    private List<CardExporter> exports =MTGDesktopCompanionControler.getInstance().getDeckExports();
	    static final Logger logger = LogManager.getLogger(ExportsTreeTableModel.class.getName());
	    private final static String[] COLUMN_NAMES = {"Exporter","Value","Enabled"};
		

	    
	    public ExportsTreeTableModel() {
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
	        if (parent instanceof CardExporter) {
	        	CardExporter dept = (CardExporter) parent;
	            return dept.getProperties().size();
	        }
	        return exports.size();
	    }

	    @Override
	    public Object getChild(Object parent, int index) 
	    {
	    	  if (parent instanceof CardExporter) {
	        	CardExporter dept = (CardExporter) parent;
	            return getPropByIndex(dept,index);
	        }
	        return new ArrayList(exports).get(index);
	    }

	    private Entry<String,Object> getPropByIndex(CardExporter dept, int index)
	    {
	    	return (Map.Entry<String,Object>)dept.getProperties().entrySet().toArray()[index];
	    }
	    
	    
	    // This is not called in the JTree's default mode: use a native implementation.
	    @Override
	    public int getIndexOfChild(Object parent, Object child) {
	    	CardExporter dept = (CardExporter) parent;
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
	       if (node instanceof CardExporter) 
	       {
	    	   CardExporter prov = (CardExporter) node;
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
	        
	        if(node instanceof CardExporter )
	        {
	        	selectedProvider=(CardExporter)node;
	        	if(column==2)
	        	{
	        		selectedProvider.enable(Boolean.parseBoolean(strValue));
	        		MTGDesktopCompanionControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
	        		
	        		
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



		public void setSelectedNode(CardExporter pathComponent) {
			selectedProvider=pathComponent;
		}
}
