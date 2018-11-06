package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CollectionAnalyzerTreeTableModel extends AbstractTreeTableModel {

	private static final String[] columnsNames = { "EDITION","PRICE" };
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private Map<MagicEdition,List<CardShake>> editions;
	
	
	public CollectionAnalyzerTreeTableModel() {
		super("T");
		editions = new TreeMap<>();
	}
	

	
	public void saveRow(MagicEdition ed, List<CardShake> loadFromCache) {
		editions.put(ed, loadFromCache);
	}
	
	
	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(columnsNames[column]);
	}
	
	@Override
	public int getColumnCount() {
		return columnsNames.length;
	}
	
	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof MagicEdition)
		{
			switch (column) 
			{
				case 0:return node;
				case 1: return total((MagicEdition)node);
				default : return "";
			}
		}
		else if(node instanceof CardShake)
		{
			
			switch (column) 
			{
				case 0:return node;
				case 1: return ((CardShake)node).getPrice();
				default : return "";
			}
		}
		return "";
	}
	
	
	//todo : currency values of total
	Double total;
	private Double total(MagicEdition node) {
		
		total=0.0;
		editions.get(node).forEach(cs->total=total+cs.getPrice());
		return total;
		
	}

	@Override
	public Object getChild(Object parent, int i) {
		if (parent instanceof MagicEdition) {
			return editions.get(parent).get(i);
		}
		return new ArrayList<>(editions.keySet()).get(i);
	}
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof MagicEdition) {
			return editions.get((MagicEdition)parent).size();
		}
		return editions.size();
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		logger.debug("getIndexOfChild("+parent+","+child+")");
		return 0;
	}
	
	
	@Override
	public boolean isLeaf(Object node) {
		
		if(node instanceof String)
			return false;
		
		return !(node instanceof MagicEdition);
	}
	
	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}


}
