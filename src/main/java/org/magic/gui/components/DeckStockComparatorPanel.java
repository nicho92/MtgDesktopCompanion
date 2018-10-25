package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.DeckStockComparisonModel;
import org.magic.gui.renderer.MagicCollectionIconListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class DeckStockComparatorPanel extends JPanel {
	
	private JComboBox<MagicCollection> cboCollections;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicDeck currentDeck;
	private DeckStockComparisonModel model;
	private JButton btnCompare;
	private AbstractBuzyIndicatorComponent buzyLabel;
	public void setCurrentDeck(MagicDeck c) {
		this.currentDeck = c;
	}
	
	public DeckStockComparatorPanel() {
		initGUI();
		initActions();
	}

	private void initGUI() {
		
		setLayout(new BorderLayout(0, 0));
		btnCompare = new JButton("Compare");
		JPanel panneauHaut = new JPanel();
		JPanel panneauBas = new JPanel();
		cboCollections = UITools.createComboboxCollection();
		buzyLabel = AbstractBuzyIndicatorComponent.createProgressComponent();
		model = new DeckStockComparisonModel();
		JXTable table = new JXTable();
		
		
		table.setModel(model);
		
		
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(cboCollections);
		panneauHaut.add(btnCompare);
		panneauHaut.add(buzyLabel);
		add(panneauBas, BorderLayout.SOUTH);
		
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
				Integer val = (Integer)value;
				if(column==4)
				{
					JLabel c = new JLabel(value.toString());
					c.setOpaque(true);
					if(val==0)
						c.setBackground(Color.GREEN);
					else
						c.setBackground(Color.RED);
						
					return c;
					
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row,column);
				
			}
		});
		
		
		try {
			cboCollections.setSelectedItem(new MagicCollection(MTGControler.getInstance().get("default-library")));
		} catch (Exception e) {
			logger.error("Error retrieving collections",e);
		}
		
		table.packAll();
		
	}

	private void initActions() {
		
		btnCompare.addActionListener(ae-> {
			model.removeAll();
			if(currentDeck!=null)
			{
				MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				buzyLabel.start(currentDeck.getMap().entrySet().size());
				
				
				ThreadManager.getInstance().execute(()->{
						currentDeck.getMap().entrySet().forEach(entry->
						{
							try {
								boolean has = MTGControler.getInstance().getEnabled(MTGDao.class).listCollectionFromCards(entry.getKey()).contains(col);
								List<MagicCardStock> stocks = MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(entry.getKey(), col,false);
								int qty = currentDeck.getMap().get(entry.getKey());
								model.addRow(entry.getKey(),qty,has, stocks);
								buzyLabel.progress();
							} catch (SQLException e) {
								logger.error(e);
								buzyLabel.end();
							}
						});
						buzyLabel.end();
						
				}, "compare deck and stock");
				
				
			}
		});
		
	}


}
