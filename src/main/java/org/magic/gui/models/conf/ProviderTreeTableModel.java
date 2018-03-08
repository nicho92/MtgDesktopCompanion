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

public class ProviderTreeTableModel<T extends MTGPlugin> extends AbstractTreeTableModel {

	
	private String[] columnsNames = {
										MTGControler.getInstance().getLangService().getCapitalize("PROVIDERS"),
										MTGControler.getInstance().getLangService().getCapitalize("VALUE"),
										"Version",
										MTGControler.getInstance().getLangService().getCapitalize("ENABLED")
									};
	
	private static final int ENABLED_INDEX=3;
	
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private MTGPlugin selectedProvider = null;
	private List<T> listElements ;
	private boolean multipleSelection=false;
	

	public ProviderTreeTableModel(boolean multipleSelection,List<T> listPlugins) {
		super(new Object());
		this.multipleSelection=multipleSelection;
		listElements=listPlugins;
	}
	
    protected int getPosition(Entry k, Properties p)
    {
    	for(int i=0;i<p.keySet().size();i++)
    	{
    		if(p.keySet().toArray()[i].toString().equals(k.getKey()))
    			return i;
    	}
    	return -1;
    }
    

    @Override
    public Object getChild(Object parent, int index) 
    {
    	  if (parent instanceof MTGPlugin) {
        	T dept = (T) parent;
            return getPropByIndex(dept,index);
        }
        return new ArrayList<T>(listElements).get(index);
    }


    protected Entry<String,Object> getPropByIndex(MTGPlugin dept, int index)
    {
    	return (Map.Entry<String,Object>)dept.getProperties().entrySet().toArray()[index];
    }
  
    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof MTGPlugin) {
        	T dept = (T) parent;
           return dept.getProperties().size();
        }
        return listElements.size();
    }
    
    
    @Override
    public void setValueAt(Object value, Object node, int column) {
    	
        String strValue = String.valueOf(value);
        
        if(node instanceof MTGPlugin )
        {
        	selectedProvider=(T)node;
        	if(column==ENABLED_INDEX)
        	{
        		selectedProvider.enable(Boolean.parseBoolean(strValue));
        		MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
        		
        		if(!multipleSelection)
	        		for(T plugin : listElements)
	        		{
	        			if(plugin!=selectedProvider)
	        			{
	        				plugin.enable(false);
	        				MTGControler.getInstance().setProperty(plugin, plugin.isEnable());
	        	        	
	        			}
	        		}
        		
        	}
        }
        if(node instanceof Entry && (column==1) )
	    	{
	        	String k = (String)((Entry)node).getKey();
	        	selectedProvider.setProperty(k, strValue);
	        	logger.debug("put " + k+"="+strValue + " to " + selectedProvider);
	        	((Entry)node).setValue(strValue);
	        	selectedProvider.save();
	    	}    
   }
    
    
    
    
    @Override
    public Object getValueAt(Object node, int column) {
       if (node instanceof MTGPlugin) 
       {
    	   MTGPlugin prov = (MTGPlugin) node;
            switch (column) {
                case 0:return prov.getName();
                case 1:return prov.getStatut();
                case 2:return prov.getVersion();
                case 3: return prov.isEnable();
                default : return"";
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
                default : 
                	return"";
            }
        }
        return null;
    }
    
    
    @Override
	public int getIndexOfChild(Object parent, Object child) {
    	MTGPlugin dept = (MTGPlugin) parent;
        Entry k = (Entry) child;
        return getPosition(k,dept.getProperties());
    }
    
	
	@Override
    public int getColumnCount() {
        return columnsNames.length;
    }
	 
	@Override
    public String getColumnName(int column) {
        return columnsNames[column];
    }
	


    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Entry;
    }
    
    
    @Override
    public Class<?> getColumnClass(int column) {
    	if(column==ENABLED_INDEX)
    		return Boolean.class;
    	
    	return super.getColumnClass(column);
    }


    
    @Override
    public boolean isCellEditable(Object node, int column) {
    	
    	System.out.println(node +" " + column);
        return  (node instanceof Entry && column == 1) || (column==3);
    }

 	public void setSelectedNode(MTGPlugin pathComponent) {
		selectedProvider=pathComponent;
	}

}
