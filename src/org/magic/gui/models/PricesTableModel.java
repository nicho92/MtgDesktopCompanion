package org.magic.gui.models;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.tools.MagicFactory;

public class PricesTableModel extends AbstractTreeTableModel 
{
    private final static String[] COLUMN_NAMES = {"Provider","Value","Enabled"};
    
    private List<MagicPricesProvider> pricersList = MagicFactory.getInstance().getListPricers();

    public PricesTableModel() {
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
        if (node instanceof Map && column == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Map;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof MagicPricesProvider) {
        	MagicPricesProvider dept = (MagicPricesProvider) parent;
            return dept.getProperties().size();
        }
        return pricersList.size();
    }

    @Override
    public Object getChild(Object parent, int index) {
    	  if (parent instanceof MagicPricesProvider) {
        	MagicPricesProvider dept = (MagicPricesProvider) parent;
            return getPropByIndex(dept.getProperties(),index);
        }
        return pricersList.get(index);
    }

    private Map<String,Object> getPropByIndex(Properties p, int index)
    {
    	String k = (String) p.keySet().toArray()[index];
    	HashMap<String, Object> prop = new HashMap<String,Object>();
    	prop.put(k, p.get(k));
    	return prop;
    }
    
    
    // This is not called in the JTree's default mode: use a native implementation.
    @Override
    public int getIndexOfChild(Object parent, Object child) {
    	MagicPricesProvider dept = (MagicPricesProvider) parent;
        String k = (String) child;
        
        return getPosition(k,dept.getProperties());
    }
    
    private int getPosition(String k, Properties p)
    {
    	for(int i=0;i<p.keySet().size();i++)
    	{
    		if(p.keySet().toArray()[i].toString().equals(k))
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
                case 2: return true;
            }
        } 
        else if (node instanceof Map) 
        {
        	Map emp = (Map) node;
            switch (column) {
                case 0:
                    return emp.keySet().iterator().next();
                case 1:
                    return emp.values().iterator().next();
            }
        }
        return null;
    }

    public void setValueAt(Object value, Object node, int column) {
    	
    	System.out.println(value +" " + node + " "+ column);
    	
//        String strValue = (String) value;
//        MagicPricesProvider prov = null;
//        
//        if(node instanceof MagicPricesProvider )
//        	prov=(MagicPricesProvider)node;
//        
//        if (node instanceof String) {
//            String emp = (String) node;
//            	prov.setProperties(emp, strValue);
//           }
        }
    
    
    @Override
    public Class<?> getColumnClass(int column) {
    	if(column==2)
    		return Boolean.class;
    	
    	return super.getColumnClass(column);
    }
    
}