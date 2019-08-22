package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.EditionPriceVariations;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.gui.renderer.CardShakeTreeCellRenderer;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.services.workers.CollectionAnalyzerWorker;
import org.magic.tools.UITools;

public class CollectionAnalyzerDashlet extends AbstractJDashlet {
	
	private static final long serialVersionUID = 1L;
	private JXTreeTable treeTable;
	private JLabel lblPrice;
	private AbstractBuzyIndicatorComponent buzy;
	private MapTableModel<MagicEdition, Date> modelCache;
	private transient CollectionEvaluator evaluator;
	private transient CollectionAnalyzerWorker sw;
	
	
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
		
		lblPrice = new JLabel();
		lblPrice.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 13));
		panelHaut.add(lblPrice);
		panelHaut.add(buzy);
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
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
		
		JButton btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		
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
			List<MagicEdition> ret = UITools.getTableSelections(tableCache,0);
			buzy.start(ret.size());
			SwingWorker<Void, Map.Entry<MagicEdition,Date>> swC = new SwingWorker<>()
			{

				@Override
				protected void done() {
					buzy.end();
				}

				@Override
				protected void process(List<Map.Entry<MagicEdition,Date>> chunks) {
					buzy.progressSmooth(chunks.size());
					
					chunks.forEach(e->modelCache.updateRow(e.getKey(),e.getValue()));
				}

				@Override
				protected Void doInBackground() throws Exception {
					for(MagicEdition ed : ret) {
						try {
							EditionPriceVariations css = evaluator.initCache(ed);
							
							if(!css.isEmpty())
							{	
								
								publish(new DefaultMapEntry<>(ed, css.getDate()));
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
					return null;
				}
		
			};
			ThreadManager.getInstance().runInEdt(swC,"update cache date for "+ret);
		});
		
		btnRefresh.addActionListener(ae-> init());
		
	}

	@Override
	public void init() {
		try {
			evaluator = new CollectionEvaluator(new MagicCollection(MTGControler.getInstance().get("default-library")));
			sw = new CollectionAnalyzerWorker(evaluator,treeTable,modelCache,buzy,lblPrice);
			ThreadManager.getInstance().runInEdt(sw,"init collection analysis dashlet");
		} 
		catch (IOException e) 
		{
			logger.error("error init analyzer",e);
		}
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}

	
	@Override
	public String getName() {
		return "Collection Analyser";
	}
	
	@Override
	protected void onDestroy() {
		if(sw!=null && !sw.isDone())
		{
			boolean ret = sw.cancel(true);
			logger.debug(sw + " is canceled"+ret );
		}
		
		
	}
}
