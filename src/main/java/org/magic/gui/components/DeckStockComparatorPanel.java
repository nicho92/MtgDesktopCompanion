package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.DeckStockComparisonModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;
import org.magic.gui.models.DeckStockComparisonModel.Line;
import javax.swing.JCheckBox;


public class DeckStockComparatorPanel extends JComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<MagicCollection> cboCollections;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicDeck currentDeck;
	private DeckStockComparisonModel model;
	private JButton btnCompare;
	private AbstractBuzyIndicatorComponent buzyLabel;
	private DeckPricePanel pricesPan;
	private JCheckBox chkCalculate ;
	
	
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
		cboCollections = UITools.createComboboxCollection();
		buzyLabel = AbstractBuzyIndicatorComponent.createProgressComponent();
		model = new DeckStockComparisonModel();
		JXTable table = new JXTable();
		JSplitPane pan = new JSplitPane();
		pan.setDividerLocation(0.5);
		pan.setResizeWeight(0.5);
		
		pan.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pricesPan = new DeckPricePanel();
		
		table.setModel(model);
		
		
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(cboCollections);
		panneauHaut.add(btnCompare);
		panneauHaut.add(buzyLabel);
		
		chkCalculate = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("CALCULATE_PRICES"));
		panneauHaut.add(chkCalculate);
		
		pan.setLeftComponent(new JScrollPane(table));
		pan.setRightComponent(pricesPan);
		
		add(pan,BorderLayout.CENTER);
		
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
			model.clear();
			if(currentDeck!=null)
			{
				MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				buzyLabel.start(currentDeck.getMap().entrySet().size());
				SwingWorker<Void, MagicCard> sw = new SwingWorker<>()
						{
						@Override
						protected Void doInBackground() throws Exception {
							currentDeck.getMap().entrySet().forEach(entry->
							{
								try {
									boolean has = MTGControler.getInstance().getEnabled(MTGDao.class).listCollectionFromCards(entry.getKey()).contains(col);
									List<MagicCardStock> stocks = MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(entry.getKey(), col,false);
									int qty = currentDeck.getMap().get(entry.getKey());
									model.addItem(entry.getKey(),qty,has, stocks);
									publish(entry.getKey());
								} catch (SQLException e) {
									logger.error(e);
								}
							});
							
							return null;
						}

						@Override
						protected void done() {
							buzyLabel.end();
							pricesPan.initDeck(MagicDeck.toDeck(model.getItems().stream().filter(l->l.getResult()>0).map(Line::getMc).collect(Collectors.toList())));
							if(chkCalculate.isSelected())
								pricesPan.getBtnCheckPrice().doClick();
							
						}

						@Override
						protected void process(List<MagicCard> chunks) {
							buzyLabel.progressSmooth(chunks.size());
						}
				};
				
				
				ThreadManager.getInstance().runInEdt(sw, "compare deck and stock");
				
				
			}
		});
		
	}


}
