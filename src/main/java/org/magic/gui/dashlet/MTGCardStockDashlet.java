package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;
import org.magic.gui.abstracts.charts.AbstractChartComponent;
import org.magic.gui.components.editor.JCheckableListBox;
import org.magic.gui.models.MapTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

import com.google.common.collect.Lists;

public class MTGCardStockDashlet extends AbstractJDashlet {
	

	private static final long serialVersionUID = 1L;
	private static final String PROPERTY = "PROPERTY";
	private JCheckBox chkSumOrTotal;
	private JComboBox<String> cboProperty;
	private AbstractChartComponent<MTGCardStock> chart;
	private MapTableModel<String,Double> tableModel;
	private JCheckableListBox<MTGCollection> lstCollections;
	
	private List<MTGCardStock> cache = new ArrayList<>();

	
	@Override
	public String getCategory() {
		return "Stock";
	}

	
	private Map<String,Double> calculate(List<MTGCardStock> items) {
		var res = new HashMap<String, Double>();
		var property = cboProperty.getSelectedItem().toString();
		
		if(chkSumOrTotal.isSelected())
			items.forEach(mcs->res.compute(BeanTools.readProperty(mcs, property).toString(), (k,v)->(v==null)?1:v+mcs.getQte()));
		else
			items.forEach(mcs->res.compute(BeanTools.readProperty(mcs, property).toString(), (k,v)->(v==null)?1:v+mcs.getPrice()));
		
		return res;
	}

	
	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		chkSumOrTotal = new JCheckBox("Count");
		lstCollections = new JCheckableListBox<>();
		var panelMenu = new JPanel();
		var pane = new JTabbedPane();
		tableModel = new MapTableModel<>();
		var table = UITools.createNewTable(tableModel,true);
		var btnReload = new JButton(MTGConstants.ICON_REFRESH);
		cboProperty = UITools.createCombobox(Lists.newArrayList("product.edition",
				"product.rarity",
				"product.types[0]",
				"condition",
				"language",
				"foil"));
		
		
		try {
			for(var col : MTG.getEnabledPlugin(MTGDao.class).listCollections())
				lstCollections.addElement(col, true);
			
		} catch (SQLException e) {
			logger.error("Error loading collections",e);
		}
		
		panelMenu.add(lstCollections);
		panelMenu.add(cboProperty);
		panelMenu.add(chkSumOrTotal);
		panelMenu.add(btnReload);
		panelMenu.add(buzy);
		
		
		chart = new Abstract3DPieChart<MTGCardStock, String>(true) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public PieDataset3D<String> getDataSet() {
				var dataset = new StandardPieDataset3D<String>();
				calculate(items).entrySet().forEach(e->dataset.add(e.getKey(),e.getValue()));
				return dataset;
			}

		
			@Override
			public boolean showLegend() {
				return false;
			}
			
			
			@Override
			public String getTitle() {
				return "Cards Stock";
			}
		}; 
		
		UITools.addTab(pane, chart);
		UITools.addTab(pane, MTGUIComponent.build(new JScrollPane(table), "Data", MTGConstants.ICON_TAB_STOCK));
		
		
		getContentPane().add(panelMenu, BorderLayout.NORTH);			
		getContentPane().add(pane,BorderLayout.CENTER);
		
		
		btnReload.addActionListener(al->{
				cache = new ArrayList<>();
				init();
		});
		
		cboProperty.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				init();
		});
		
		chkSumOrTotal.addItemListener(ie -> {
			init();
		});
		
		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			if(!getString(PROPERTY).isEmpty())
				cboProperty.setSelectedItem(getString(PROPERTY));


			chkSumOrTotal.setSelected(getString("COUNT").equals("true"));
			setBounds(r);
		}

	}
	
	
	
	@Override
	public void init() {
		setProperty(PROPERTY, String.valueOf(cboProperty.getSelectedItem()));
		setProperty("COUNT", String.valueOf(chkSumOrTotal.isSelected()));
		
		buzy.start();
		var sw = new AbstractObservableWorker<List<MTGCardStock>, MTGCard,MTGDao>(buzy,MTG.getEnabledPlugin(MTGDao.class)) {

			@Override
			protected List<MTGCardStock> doInBackground() throws Exception {
				
				if(cache.isEmpty())
					cache = plug.listStocks(lstCollections.getSelectedElements());
				
				return cache;
				
			}
					
			@Override
			protected void notifyEnd() {
				chart.init(getResult());
				tableModel.init(calculate(getResult()));
				tableModel.fireTableDataChanged();
			}
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "Refresh " + getName());
		
		
	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_STOCK;
	}

	@Override
	public String getName() {
		return "Card Stock";
	}


}