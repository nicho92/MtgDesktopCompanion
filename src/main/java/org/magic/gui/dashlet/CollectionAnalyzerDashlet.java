package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.eclipse.jetty.server.LocalConnector;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.CollectionAnalyzerTreeTableModel;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.gui.renderer.CardShakeTreeCellRenderer;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class CollectionAnalyzerDashlet extends AbstractJDashlet {
	private static final long serialVersionUID = 1L;
	private JXTreeTable treeTable;
	private transient CollectionAnalyzerTreeTableModel model;
	private JLabel lblPrice;
	private AbstractBuzyIndicatorComponent buzy;
	private MapTableModel<MagicEdition, Date> modelCache;
	private transient CollectionEvaluator evaluator;
	
	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		modelCache = new MapTableModel<>();
		modelCache.setColumnNames("EDITION", "DATE");
		JXTable tableCache = new JXTable();
		tableCache.setModel(modelCache);
		
		JPanel panelHaut = new JPanel();
		getContentPane().add(panelHaut, BorderLayout.NORTH);
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		
		lblPrice = new JLabel("");
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelHaut.add(lblPrice);
		panelHaut.add(buzy);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panneauColl = new JPanel();
		tabbedPane.addTab("Collection", null, panneauColl, null);
		
		treeTable = new JXTreeTable();
		CardShakeTreeCellRenderer ren = new CardShakeTreeCellRenderer();
		treeTable.setDefaultRenderer(Object.class, ren);
		treeTable.setTreeCellRenderer(ren);
		
		panneauColl.setLayout(new BorderLayout(0, 0));
		panneauColl.add(new JScrollPane(treeTable));
		
		JPanel panneauh = new JPanel();
		panneauColl.add(panneauh, BorderLayout.NORTH);
		
		JButton btnRefresh = new JButton("Refresh");
		
		panneauh.add(btnRefresh);
		
		JPanel panelCacheDetail = new JPanel();
		tabbedPane.addTab("Cache", null, panelCacheDetail, null);
		panelCacheDetail.setLayout(new BorderLayout(0, 0));
		
		
		panelCacheDetail.add(new JScrollPane(tableCache));
		
		JPanel panel = new JPanel();
		panelCacheDetail.add(panel, BorderLayout.NORTH);
		
		JButton btnUpdateCache = new JButton("Update selected Cache");
		
		panel.add(btnUpdateCache);
		

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);
		}
		
		btnUpdateCache.addActionListener(ae->
		{
			List<MagicEdition> ret = UITools.getTableSelection(tableCache,0);
			logger.debug("updating " + ret);
			buzy.start(ret.size());
			ThreadManager.getInstance().execute(()->{
				for(MagicEdition ed : ret) {
					try {
						List<CardShake> css = evaluator.initCache(ed);
						
						if(!css.isEmpty())
						{	
							modelCache.updateRow(ed, css.get(0).getDateUpdate());
						 	buzy.progress();
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
				buzy.end();
			}, "Loading treeCardShake");
			
		
		});
		
		btnRefresh.addActionListener(ae-> init());
		
	}

	@Override
	public void init() {
		model = new CollectionAnalyzerTreeTableModel();
		modelCache.removeAll();
		ThreadManager.getInstance().execute(()->{
			
			try {
				evaluator = new CollectionEvaluator(new MagicCollection(MTGControler.getInstance().get("default-library")));
				evaluator.addObserver(buzy);
				List<MagicEdition> eds = evaluator.getEditions();
				Collections.sort(eds);
				buzy.start(eds.size());
				
				for(MagicEdition ed : eds)
				{
					modelCache.addRow(ed, evaluator.getCacheDate(ed));
					List<CardShake> list = new ArrayList<>(evaluator.prices(ed).values());
					Collections.sort(list);
					
					model.saveRow(ed,list);
				}
	
			Double total = evaluator.total();
			lblPrice.setText("Value : " + UITools.formatDouble(total) + " " + MTGControler.getInstance().getEnabled(MTGDashBoard.class).getCurrency().getCurrencyCode());
			
			
			
			buzy.end();
			treeTable.setTreeTableModel(model);
			
			} catch (IOException e) {
				logger.error(e);
				buzy.end();
			}
			
			
		}, "Loading treeCardShake");
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
