package org.magic.gui.models;

import static org.magic.services.tools.MTG.capitalize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGEdition;
import org.magic.services.logging.MTGLogger;
public class CollectionAnalyzerTreeTableModel extends AbstractTreeTableModel {

	private static final String[] columnsNames = { "EDITION","PRICE" };
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private Map<MTGEdition,List<CardShake>> editions;


	public CollectionAnalyzerTreeTableModel() {
		super("T");
		editions = new TreeMap<>();
	}



	public void saveRow(MTGEdition ed, List<CardShake> list) {
		
		if(list.stream().mapToDouble(CardShake::getPrice).sum()>0)
			editions.put(ed, list);
	}


	@Override
	public String getColumnName(int column) {
		return capitalize(columnsNames[column]);
	}

	@Override
	public int getColumnCount() {
		return columnsNames.length;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof MTGEdition ed)
		{
			switch (column)
			{
				case 0:return node;
				case 1: return total(ed);
				default : return "";
			}
		}
		else if(node instanceof CardShake cs)
		{

			switch (column)
			{
				case 0:return node;
				case 1: return cs.getPrice();
				default : return "";
			}
		}
		return "";
	}

	Double total;
	private Double total(MTGEdition node) {

		total=0.0;
		editions.get(node).forEach(cs->total=total+cs.getPrice());
		return total;

	}

	@Override
	public Object getChild(Object parent, int i) {
		if (parent instanceof MTGEdition) {
			return editions.get(parent).get(i);
		}
		return new ArrayList<>(editions.keySet()).get(i);
	}
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof MTGEdition) {
			return editions.get(parent).size();
		}
		return editions.size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		logger.debug("getIndexOfChild({},{})",parent,child);
		return 0;
	}


	@Override
	public boolean isLeaf(Object node) {

		if(node instanceof String)
			return false;

		return !(node instanceof MTGEdition);
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}


}
