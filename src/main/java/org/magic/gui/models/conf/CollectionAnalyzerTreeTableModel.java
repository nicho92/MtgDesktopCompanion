package org.magic.gui.models.conf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CollectionAnalyzerTreeTableModel extends AbstractTreeTableModel {

	private static final String[] columnsNames = { "EDITION","PRICE" };
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private CollectionEvaluator evaluator;
	private List<MagicEdition> editions;
	
	
	public CollectionAnalyzerTreeTableModel(MagicCollection c) {
		super(new String(""));
		try {
			evaluator = new CollectionEvaluator(c);
			editions = evaluator.getEditions();
			Collections.sort(editions);
		} catch (IOException e) {
			logger.error("couldn't not init evaluator",e);
		}
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
				case 1: return evaluator.total((MagicEdition)node);
				default : return "";
			}
		}
		else if(node instanceof CardShake)
		{
			switch (column) 
			{
				case 0:return ((CardShake) node);
				case 1: return ((CardShake)node).getPrice();
				default : return "";
			}
		}
		return "";
	}

	@Override
	public Object getChild(Object parent, int i) {
		if (parent instanceof MagicEdition) {
			List<CardShake> l = new ArrayList<>(evaluator.prices((MagicEdition)parent).values());
			return l.get(i);
		}
		return editions.get(i);
	}
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof MagicEdition) {
			return evaluator.prices((MagicEdition)parent).size()-1;
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
	
	public double getTotalPrice()
	{
		
		return evaluator.total();
	}
	
	
}
