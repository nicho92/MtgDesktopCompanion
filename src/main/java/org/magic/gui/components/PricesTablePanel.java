package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.swing.DefaultRowSorter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;
public class PricesTablePanel extends JPanel {
	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private CardsPriceTableModel model;
	private JXTable tablePrices;
	private AbstractBuzyIndicatorComponent lblLoading;
	private transient DefaultRowSorter<TableModel, Integer> sorterPrice;
	private transient List<RowSorter.SortKey> sortKeys;
	private MagicCard currentCard;
	private boolean foilOnly;
	
	public PricesTablePanel() {
		var panel = new JPanel();
		lblLoading = AbstractBuzyIndicatorComponent.createProgressComponent();
		
		panel.setPreferredSize(new Dimension(0,32));
		model = new CardsPriceTableModel();
		tablePrices = UITools.createNewTable(model);
		UITools.initTableFilter(tablePrices);
		sorterPrice = new TableRowSorter<>(model);
		
		sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorterPrice.setSortKeys(sortKeys);
		
		
		
		setLayout(new BorderLayout(0, 0));
		
		tablePrices.setRowSorter(sorterPrice);

		for(var i : model.defaultHiddenColumns())
			tablePrices.getColumnExt(model.getColumnName(i)).setVisible(false);
	
		
		
		add(panel, BorderLayout.NORTH);
		
		
		panel.add(lblLoading);
		add(new JScrollPane(tablePrices), BorderLayout.CENTER);
		
		tablePrices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();
						MagicPrice url = UITools.getTableSelection(tablePrices, 0);
						UITools.browse(url.getUrl());
				}

			}
		});
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentCard,foilOnly);
			}

		});
		
	}
	
	public void init(MagicCard card)
	{
		init(card,false);
	}
	

	public void init(MagicCard card,boolean foilOnly)
	{
		currentCard = card;
		this.foilOnly=foilOnly;
		
		if(currentCard==null)
			return;
			
		if(isVisible())
		{

			model.clear();
			
			
			List<MTGPricesProvider> providers = listEnabledPlugins(MTGPricesProvider.class);
			lblLoading.start(providers.size());
			
			var cdl = new CountDownLatch(listEnabledPlugins(MTGPricesProvider.class).size());
	
			
			for(MTGPricesProvider prov : listEnabledPlugins(MTGPricesProvider.class))
			{
				SwingWorker<List<MagicPrice>, MagicPrice> sw = new SwingWorker<>()
				{
					@Override
					protected List<MagicPrice> doInBackground() throws Exception {
						
						List<MagicPrice> list = new ArrayList<>();
						lblLoading.setText(capitalize("LOADING_PRICES") + " : " + currentCard + "("+currentCard.getCurrentSet()+")" );
						
						
							try {
							
								List<MagicPrice> l = prov.getPrice(currentCard);
								
								if(foilOnly)
									l = l.stream().filter(MagicPrice::isFoil).collect(Collectors.toList());
								
								
								publish(l.toArray(new MagicPrice[l.size()]));
								list.addAll(l);
							}
							catch(Exception e)
							{
								logger.error("error with " + prov + ":" + e);
							}
						
						return list;
					}
					
					
					@Override
					protected void process(List<MagicPrice> chunks) {
						
						model.addItems(chunks);
						
					}
					
					@Override
					protected void done() {
						lblLoading.progress();
						cdl.countDown();
						
						if(cdl.getCount()==0)
							lblLoading.end();
						
					}
			
				};
				ThreadManager.getInstance().runInEdt(sw,"loading prices");
			}
		}
	}
	
	public List<MagicPrice> getPrices()
	{
		return model.getItems();
	}
	
	
}
