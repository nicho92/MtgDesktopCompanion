package org.magic.gui.models;

import static org.magic.tools.MTG.capitalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.beans.MagicPrice;
import org.magic.services.logging.MTGLogger;

public class GroupedPriceTreeTableModel extends AbstractTreeTableModel {

	private String[] columnsNames = { capitalize("NAME"),capitalize("QTY"),capitalize("VALUE"),capitalize("LANG"),capitalize("QUALITY"),capitalize("FOIL") };
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	private Map<String, List<MagicPrice>> listElements;

	
	public GroupedPriceTreeTableModel() {
		super(new Object());
		listElements = new HashMap<>();
	}
	
	public void init(Map<String, List<MagicPrice>> map)
	{
		listElements= map;
		modelSupport.fireNewRoot();
	}
	
	
	
	
	public void addItem(String key, List<MagicPrice> list)
	{
		listElements.computeIfAbsent(key, v->new ArrayList<>()).addAll(list);
		modelSupport.fireNewRoot();
	}
	
	
	@Override
	public Class<?> getColumnClass(int column) {
		if(column==5)
			return Boolean.class;
		
		if(column==2)
			return Double.class;
		
		return super.getColumnClass(column);
	}
	
	protected int getPosition(MagicPrice k, List<MagicPrice> p) {
		for (var i = 0; i < p.size(); i++) {
			if (p.get(i).equals(k))
				return i;
		}
		return -1;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof String) {
			return listElements.get(parent).get(index);
		}
		return new ArrayList<>(listElements.keySet()).get(index);
	}


	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof String) {
			return listElements.get(parent).size();
		}
		return listElements.size();
	}
	
	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof String) {
			switch (column) 
			{
			case 0:
				return node;
			case 1:
				return listElements.get(node).size();
			case 2:
				return listElements.get(node).stream().mapToDouble(MagicPrice::getValue).sum();
			default:
				return "";
			}
		} 
		else if (node instanceof MagicPrice emp) {
			switch (column) {
			case 0:
				return emp;
			case 1:
				return 1;
			case 2:
				return emp.getValue();
			case 3:
				return emp.getLanguage();
			case 4:
				return emp.getQuality();
			case 5:
				return emp.isFoil();
			default:
				return "";
			}
		}
		return null;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
			try {
				MagicPrice k = (MagicPrice) child;
				return getPosition(k, listElements.get(parent));
			}
			catch(ClassCastException e)
			{
				return 0;
			}
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
		return node instanceof MagicPrice;
	}

	
	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}

}
