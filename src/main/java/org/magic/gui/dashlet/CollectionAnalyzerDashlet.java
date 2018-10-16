package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.CollectionAnalyzerTreeTableModel;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.gui.renderer.CardShakeTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class CollectionAnalyzerDashlet extends AbstractJDashlet {
	

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
	
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		JDesktopPane jif = new JDesktopPane();
		CollectionAnalyzerDashlet dash = new CollectionAnalyzerDashlet();
		dash.init();
		dash.setVisible(true);
		jif.add(dash);
		
		f.getContentPane().add(jif, BorderLayout.CENTER);
		f.setSize(640, 480);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		
	}
	
	
	private JXTreeTable treeTable;
	private CollectionAnalyzerTreeTableModel model;
	private JLabel lblPrice;
	private AbstractBuzyIndicatorComponent buzy;
	private JTabbedPane tabbedPane;
	private JPanel panelCacheDetail;
	private JTable tableCache;
	private MapTableModel<MagicEdition, Date> modelCache;
	private JPanel panel;
	private JButton btnUpdateCache;
	
	
	public CollectionAnalyzerDashlet() {
		super();
		initGUI();
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		modelCache = new MapTableModel<>();
		tableCache = new JTable(modelCache);
		
		JPanel panelHaut = new JPanel();
		getContentPane().add(panelHaut, BorderLayout.NORTH);
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		lblPrice = new JLabel("");
		panelHaut.add(lblPrice);
		panelHaut.add(buzy);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		treeTable = new JXTreeTable();
		treeTable.setDefaultRenderer(Object.class, new CardShakeTreeCellRenderer());
		tabbedPane.addTab("Evaluation", null, new JScrollPane(treeTable), null);
		
		panelCacheDetail = new JPanel();
		tabbedPane.addTab("Cache", null, panelCacheDetail, null);
		panelCacheDetail.setLayout(new BorderLayout(0, 0));
		
		
		panelCacheDetail.add(new JScrollPane(tableCache));
		
		panel = new JPanel();
		panelCacheDetail.add(panel, BorderLayout.NORTH);
		
		btnUpdateCache = new JButton("Update selected Cache");
		
		panel.add(btnUpdateCache);
		

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);
		}
		
		
		
		
		
		btnUpdateCache.addActionListener(ae->
		{
			
			ThreadManager.getInstance().execute(()->{
				List<MagicEdition> ret = UITools.getSelects(tableCache,0);
				buzy.start(ret.size());
				for(MagicEdition ed : ret)
					try {
						List<CardShake> css = model.getEvaluator().initCache(ed);
						modelCache.removeRow(ed);
						if(!css.isEmpty())
							modelCache.addRow(ed, css.get(0).getDateUpdate());
						buzy.progress();
					} catch (Exception e) {
						logger.error(e);
					}
		
			}, "Loading treeCardShake");
			buzy.end();
		
		});
		
	}

	@Override
	public void init() {
		ThreadManager.getInstance().execute(()->{
			buzy.start();
			model = new CollectionAnalyzerTreeTableModel(new MagicCollection("Library"));
			treeTable.setTreeTableModel(model);
			lblPrice.setText("Value : " + UITools.formatDouble(model.getTotalPrice()));
			
			model.getEditions().forEach(ed->modelCache.addRow(ed, model.getEvaluator().getCacheDate(ed)));
			
			
			buzy.end();
			
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
