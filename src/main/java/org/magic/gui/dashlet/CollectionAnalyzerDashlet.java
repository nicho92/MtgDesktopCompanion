package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.MapTableModel;
import org.magic.gui.renderer.CardShakeTreeCellRenderer;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.CollectionAnalyzerWorker;

public class CollectionAnalyzerDashlet extends AbstractJDashlet {


	private static final long serialVersionUID = 1L;
	private JXTreeTable treeTable;
	private JLabel lblPrice;
	private AbstractBuzyIndicatorComponent buzy;
	private MapTableModel<MagicEdition, Date> modelCache;
	private transient CollectionEvaluator evaluator;
	private transient CollectionAnalyzerWorker sw;
	private JSlider slider ;


	@Override
	public String getCategory() {
		return "Collection";
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		modelCache = new MapTableModel<>();
		modelCache.setColumnNames("EDITION", "DATE");
		JXTable tableCache = UITools.createNewTable(modelCache);
		tableCache.setModel(modelCache);
		var lblValue = new JLabel();
		var btnExpand = UITools.createBindableJButton("Expand",MTGConstants.ICON_SMALL_CHECK,KeyEvent.VK_E,"expands");
		var panelHaut = new JPanel();
		getContentPane().add(panelHaut, BorderLayout.NORTH);
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();

		lblPrice = new JLabel();
		lblPrice.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 13));
		panelHaut.add(lblPrice);
		panelHaut.add(buzy);
		
		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		var panneauColl = new JPanel();
		tabbedPane.addTab("Collection", null, panneauColl, null);

		treeTable = new JXTreeTable();
		var ren = new CardShakeTreeCellRenderer();
		treeTable.setDefaultRenderer(Object.class, ren);
		treeTable.setTreeCellRenderer(ren);

		panneauColl.setLayout(new BorderLayout(0, 0));
		panneauColl.add(new JScrollPane(treeTable));

		var panneauh = new JPanel();
		panneauColl.add(panneauh, BorderLayout.NORTH);

		var btnRefresh = new JButton(MTGConstants.ICON_REFRESH);

		panneauh.add(btnRefresh);
		panneauh.add(btnExpand);
		var panelCacheDetail = new JPanel();
		tabbedPane.addTab("Cache", null, panelCacheDetail, null);
		panelCacheDetail.setLayout(new BorderLayout(0, 0));


		var panelPriceMin = new JPanel();
		slider = new JSlider(0, 100);

		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		panelPriceMin.add(slider);
		panelPriceMin.add(lblValue);
		
		btnExpand.addActionListener(al->treeTable.expandAll());
		
		slider.addChangeListener(cl->{
				setProperty("priceMin", String.valueOf(slider.getValue()));

				if(evaluator!=null)
					evaluator.setMinPrice(slider.getValue());

				lblValue.setText( String.valueOf(slider.getValue()));

		});


		tabbedPane.addTab("Prices Threshold", null, panelPriceMin, null);


		panelCacheDetail.add(new JScrollPane(tableCache));

		var panel = new JPanel();
		panelCacheDetail.add(panel, BorderLayout.NORTH);

		var btnUpdateCache = new JButton("Update selected Cache");

		panel.add(btnUpdateCache);


		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);

			try {
			slider.setValue(Integer.parseInt(getProperty("priceMin","0")));
			} catch (Exception e) {
				logger.error("can't get priceMin value", e);
			}

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
							EditionsShakers css = evaluator.initCache(ed);
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
			evaluator.setMinPrice(slider.getValue());
			sw = new CollectionAnalyzerWorker(evaluator,treeTable,modelCache,buzy,lblPrice);
			ThreadManager.getInstance().runInEdt(sw,"init collection analysis dashlet");
		}
		catch (IOException e)
		{
			logger.error("error init analyzer",e);
		}
	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}


	@Override
	public String getName() {
		return "Collection Analyser";
	}

	@Override
	public void onDestroy() {
		if(sw!=null && !sw.isDone())
		{
			boolean ret = sw.cancel(true);
			logger.debug("{} is canceled {}",sw,ret );
		}


	}
}
