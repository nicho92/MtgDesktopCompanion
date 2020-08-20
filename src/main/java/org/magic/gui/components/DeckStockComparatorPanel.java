package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.DeckStockComparisonModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;


public class DeckStockComparatorPanel extends MTGUIComponent {
	
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
	private JExportButton btnExportMissing;
	
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
		btnExportMissing = new JExportButton(MODS.EXPORT);
		JSplitPane pan = new JSplitPane();
		pan.setDividerLocation(0.5);
		pan.setResizeWeight(0.5);
		
		pan.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pricesPan = new DeckPricePanel();
		
		table.setModel(model);
		
		UITools.initCardToolTipTable(table, 0,null);
		
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(cboCollections);
		panneauHaut.add(btnCompare);
		panneauHaut.add(buzyLabel);
		
		chkCalculate = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("CALCULATE_PRICES"));
		panneauHaut.add(chkCalculate);
		
		
		btnExportMissing.setEnabled(false);
		btnExportMissing.initCardsExport(new Callable<MagicDeck>() {
			
			@Override
			public MagicDeck call() throws Exception {
				
				MagicDeck d = new MagicDeck();
				d.setName(currentDeck.getName());
				d.setDescription("Missing cards for deck " + d.getName());
				model.getItems().forEach(l->{
					d.getMain().put(l.getMc(), l.getResult());
				});
				
				return d;
			}
		}, buzyLabel);
		
		panneauHaut.add(btnExportMissing);
		
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
				buzyLabel.start(currentDeck.getMain().entrySet().size());
				SwingWorker<Void, MagicCard> sw = new SwingWorker<>()
						{
						@Override
						protected Void doInBackground() throws Exception {
							currentDeck.getMain().entrySet().forEach(entry->
							{
								try {
									boolean has = MTGControler.getInstance().getEnabled(MTGDao.class).listCollectionFromCards(entry.getKey()).contains(col);
									List<MagicCardStock> stocks = MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(entry.getKey(), col,false);
									int qty = currentDeck.getMain().get(entry.getKey());
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
					
							List<MagicCard> pricList = new ArrayList<>();
							model.getItems().stream().filter(l->l.getResult()>0).forEach(l->{
								for(int i=0;i<l.getResult();i++)
									pricList.add(l.getMc());
							});
							
							pricesPan.initDeck(MagicDeck.toDeck(pricList));
							if(chkCalculate.isSelected())
								pricesPan.getBtnCheckPrice().doClick();
							
							btnExportMissing.setEnabled(!model.isEmpty());
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

	@Override
	public String getTitle() {
		return "Deck Stock calculation";
	}


}
