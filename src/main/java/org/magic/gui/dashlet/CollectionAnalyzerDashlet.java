package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.conf.CollectionAnalyzerTreeTableModel;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class CollectionAnalyzerDashlet extends AbstractJDashlet {
	
	
	private JXTreeTable treeTable;
	private CollectionAnalyzerTreeTableModel model;
	
	
	public CollectionAnalyzerDashlet() {
		super();

		
		
		initGUI();
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		model = new CollectionAnalyzerTreeTableModel();
		treeTable = new JXTreeTable(model);
		getContentPane().add(treeTable, BorderLayout.CENTER);
		
		
	}

	@Override
	public void init() {
		

	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}

	
	@Override
	public String getName() {
		return "Collection Analyser";
	}
}
