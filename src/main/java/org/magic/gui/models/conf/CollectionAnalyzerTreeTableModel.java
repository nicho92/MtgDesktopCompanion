package org.magic.gui.models.conf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CollectionAnalyzerTreeTableModel extends AbstractTreeTableModel {

	private String[] columnsNames = { "Edition","Cards","Value" };
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private CollectionEvaluator evaluator;
	private List<MagicEdition> editions;
	
	
	public CollectionAnalyzerTreeTableModel() {
		super(new Object());
		try {
			evaluator = new CollectionEvaluator(new MagicCollection(MTGControler.getInstance().get("default-library")));
			editions = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions();
		} catch (IOException e) {
			logger.error("couldn't not init evaluator",e);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnsNames[column];
	}
	
	@Override
	public int getColumnCount() {
		return columnsNames.length;
	}
	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof MagicEdition && column==0) {
				return node;
		}	
		return new String(node+"-"+column);
	}
	@Override
	public Object getChild(Object parent, int arg1) {
		return parent;
	}
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof MagicEdition) {
			return ((MagicEdition)parent).getCardCount();
		}
		return editions.size();
	}
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean isLeaf(Object node) {
		return !(node instanceof MagicEdition);
	}
	
	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}
	
	
}
