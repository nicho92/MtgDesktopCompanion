package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.OrderEntryPanel;
import org.magic.gui.components.charts.EditionFinancialChartPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.dialog.OrderImporterDialog;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.gui.renderer.MagicEditionJLabelRenderer;
import org.magic.gui.renderer.OrderEntryRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

public class OrdersGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private ShoppingEntryTableModel model;
	private JLabel totalBuy;
	private JLabel totalSell;
	private JLabel total;
	private JLabel selectionBuy;
	private JLabel selectionSell;
	private JLabel totalSelection;
	private JXTable table;
	private OrderEntryPanel orderEntryPanel;
	private HistoryPricesPanel pricesPanel;
	private JLabel lblComparator;
	private JPanel editorPanel;
	private JPanel panelComparator;
	private JPanel chartesContainerPanel;
	private EditionFinancialChartPanel editionFinancialChartPanel;
	private AbstractBuzyIndicatorComponent buzy;
	
	private void loadFinancialBook()
	{
		SwingWorker<List<OrderEntry>, OrderEntry> sw = new SwingWorker<List<OrderEntry>, OrderEntry>()
				{

					@Override
					protected List<OrderEntry> doInBackground() throws Exception {
						return MTGControler.getInstance().getEnabled(MTGDao.class).listOrders();
					}
					
					@Override
					protected void done() {
						try {
							model.addItems(get());
							calulate(model.getItems());
						} catch (Exception e) {
							logger.error(e);
						} 
						table.packAll();
						UITools.initTableFilter(table);
					}
			
				};
			
			ThreadManager.getInstance().runInEdt(sw,"loading orders");
	}
	
	
	public OrdersGUI() {
		
		JPanel panneauBas = new JPanel();
		JPanel panneauHaut = new JPanel();
		JPanel panneauRight = new JPanel();
		table = new JXTable();
		model = new ShoppingEntryTableModel();
		JButton btnImportTransaction = new JButton(MTGConstants.ICON_IMPORT);
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		totalBuy = new JLabel(MTGConstants.ICON_DOWN);
		totalSell = new JLabel(MTGConstants.ICON_UP);
		total = new JLabel();
		totalSelection = new JLabel();
		selectionSell = new JLabel(MTGConstants.ICON_UP);
		selectionBuy=new JLabel(MTGConstants.ICON_DOWN);
		editorPanel = new JPanel();
		orderEntryPanel = new OrderEntryPanel();
		JButton btnSaveOrder = new JButton(MTGConstants.ICON_SAVE);
		JPanel panelButton = new JPanel();
		JButton btnDeleteOrder = new JButton(MTGConstants.ICON_DELETE);
		JButton btnNewEntry = new JButton(MTGConstants.ICON_NEW);
		panelComparator = new JPanel();
		panelComparator.setPreferredSize(new Dimension(10, 30));
		panelComparator.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		lblComparator = new JLabel("Values");
		editionFinancialChartPanel = new EditionFinancialChartPanel();
		JButton btnAddToCollection = new JButton(MTGConstants.ICON_MASS_IMPORT);
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		table.setModel(model);
		setLayout(new BorderLayout(0, 0));
		btnAddToCollection.setEnabled(false);
		table.setDefaultRenderer(MagicEdition.class, new MagicEditionJLabelRenderer());
		table.setDefaultRenderer(Double.class,  new OrderEntryRenderer());
		panneauRight.setPreferredSize(new Dimension(500, 1));
		editorPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		editorPanel.setLayout(new BorderLayout(0, 0));
		
		btnDeleteOrder.setEnabled(false);
		panelComparator.setLayout(new BorderLayout(0, 0));
		lblComparator.setHorizontalAlignment(SwingConstants.CENTER);
		lblComparator.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 16));
		
		
		
		panneauBas.add(totalBuy);
		panneauBas.add(totalSell);
		panneauBas.add(total);
		panneauBas.add(new JLabel(" ("));
		panneauBas.add(selectionBuy);
		panneauBas.add(selectionSell);
		panneauBas.add(totalSelection);
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnSave);
		panneauHaut.add(btnDeleteOrder);
		panneauHaut.add(btnAddToCollection);
		panneauHaut.add(buzy);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauRight,BorderLayout.EAST);
		panneauRight.setLayout(new BorderLayout(0, 0));
		editorPanel.add(orderEntryPanel, BorderLayout.CENTER);
		editorPanel.add(panelButton, BorderLayout.SOUTH);
		
		panelButton.add(btnSaveOrder);
		panelButton.add(btnNewEntry);
		
		panneauRight.add(editorPanel, BorderLayout.SOUTH);
		panneauRight.add(panelComparator, BorderLayout.NORTH);
			
		panelComparator.add(lblComparator);
		
		chartesContainerPanel = new JPanel();
		panneauRight.add(chartesContainerPanel, BorderLayout.CENTER);
		chartesContainerPanel.setLayout(new GridLayout(2, 1, 0, 0));
		pricesPanel = new HistoryPricesPanel(false);
		chartesContainerPanel.add(pricesPanel);
		chartesContainerPanel.add(editionFinancialChartPanel);
		
		
		
		
		add(panneauBas,BorderLayout.SOUTH);
		
		table.setSortOrder(2, SortOrder.DESCENDING);

		loadFinancialBook();
		
		btnAddToCollection.addActionListener(ae ->{
				JPopupMenu popupMenu = new JPopupMenu("Title");
				try {
						for(MagicCollection c : MTGControler.getInstance().getEnabled(MTGDao.class).listCollections())
						{
							JMenuItem cutMenuItem = new JMenuItem(c.getName(),MTGConstants.ICON_COLLECTION);
							popupMenu.add(cutMenuItem);
							
							
							cutMenuItem.addActionListener(event->{
								List<OrderEntry> entries = UITools.getTableSelections(table,0);
								
								List<MagicCard> toSave = new ArrayList<>();
								
								entries.stream().filter(o->o.getType()==OrderEntry.TYPE_ITEM.CARD || o.getType()==OrderEntry.TYPE_ITEM.FULLSET).forEach(order->{
									
									try {
											if(order.getType()==OrderEntry.TYPE_ITEM.CARD)
											{
												List<MagicCard> l = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(order.getDescription(), order.getEdition(), false);
												if(l.size()>1)
													logger.warn("warning, multiresults for " + order.getDescription() + " :" + l);
												
												toSave.add(l.get(0));
											}
											else if(order.getType()==OrderEntry.TYPE_ITEM.FULLSET)
											{
												toSave.addAll(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByEdition(order.getEdition()));
											}
									} catch (Exception e) {
										logger.error("can't find " + order.getDescription() +"/"+order.getEdition() + " " + e);
									}
								});
								
								
								int ret = JOptionPane.showConfirmDialog(this, "Add " + toSave.size() +" items to " + c +" ?");
								
								if(ret == JOptionPane.YES_OPTION) {
									AbstractObservableWorker<Void, MagicCard, MTGDao> sw = new AbstractObservableWorker<>(buzy, MTGControler.getInstance().getEnabled(MTGDao.class), toSave.size()) {
										@Override
										protected Void doInBackground() {
											toSave.forEach(card->{
												try {
													plug.saveCard(card, c);
												} catch (SQLException e) {
													logger.error(e);
												}
											});
											return null;
										}
									};
									
									ThreadManager.getInstance().runInEdt(sw, "saving orders in " + c);
								}
						});
							
							
						}
					} catch (Exception e1) {
						logger.error(e1);
				}
				btnAddToCollection.setComponentPopupMenu(popupMenu);
		    	Component b=(Component)ae.getSource();
		    	Point p=b.getLocationOnScreen();
		    	popupMenu.show(this,0,0);
		    	popupMenu.setLocation(p.x,p.y+b.getHeight());
		    });
		
		
		
		
		btnSaveOrder.addActionListener(ae->{
			orderEntryPanel.save();
			model.fireTableDataChanged();
		});
		
		btnNewEntry.addActionListener(ae->{
			model.addItem(orderEntryPanel.newOrderEntry());
			calulate(model.getItems());
		});
		
		
		btnDeleteOrder.addActionListener(ae->{
			
				List<OrderEntry> states = UITools.getTableSelections(table, 0);

				if(states.isEmpty())
					return;
				
				
				int res = JOptionPane.showConfirmDialog(null,MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_DELETE",states.size() + " item(s)"),
						MTGControler.getInstance().getLangService().getCapitalize("DELETE") + " ?",JOptionPane.YES_NO_OPTION);
				
				
				
				SwingWorker<Void,OrderEntry> sw = new SwingWorker<>()
				{

					@Override
					protected Void doInBackground() throws Exception {
						states.forEach(state->{
							try {
							MTGControler.getInstance().getEnabled(MTGDao.class).deleteOrderEntry(state);
							model.removeItem(state);
							} catch (Exception e) {
								logger.error("error deleting " + state,e);
							}
						});
						return null;
						
						
					}

					@Override
					protected void done() {
						calulate(model.getItems());
					}
									
				};
				
				
				if(res==JOptionPane.OK_OPTION)
					ThreadManager.getInstance().runInEdt(sw,"delete "+states.size()+" orders");
				
				
				
			
		});
			
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				try {
				OrderEntry o = UITools.getTableSelection(table, 0);
				orderEntryPanel.setOrderEntry(o);
				
				calulate(UITools.getTableSelections(table, 0));
			
				if(o.getEdition()!=null)
					editionFinancialChartPanel.init(o.getEdition());
		
				
				ThreadManager.getInstance().executeThread(()->{
						MagicCard mc=null;
						try {
							mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(o.getDescription(), o.getEdition(), false).get(0);
						}
						catch(Exception e)
						{
							//do nothing
						}	
						CardPriceVariations e;
						try {
							e = MTGControler.getInstance().getEnabled(MTGDashBoard.class).getPriceVariation(mc, o.getEdition());
						
						Double actualValue = MTGControler.getInstance().getCurrencyService().convertTo(o.getCurrency(), e.get(e.getLastDay()));
						Double paidValue = MTGControler.getInstance().getCurrencyService().convertTo(o.getCurrency(), o.getItemPrice());
						
						Double pc = UITools.roundDouble(((actualValue-paidValue)/paidValue)*100);
						String spc = ((pc>0) ? "+":"")+pc;
						
						
						lblComparator.setText(MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode() +" " + o.getTypeTransaction() + " =" + UITools.formatDouble(paidValue) + " VALUE="+UITools.formatDouble(actualValue) + " : " + spc +"%");
						if(actualValue<paidValue)
							lblComparator.setIcon((o.getTypeTransaction()==TYPE_TRANSACTION.BUY)?MTGConstants.ICON_DOWN:MTGConstants.ICON_UP);
						else if(actualValue>paidValue)
							lblComparator.setIcon((o.getTypeTransaction()==TYPE_TRANSACTION.BUY)?MTGConstants.ICON_UP:MTGConstants.ICON_DOWN);
						else
							lblComparator.setIcon(null);
						} catch (IOException e1) {
							//do nothing
						}
						pricesPanel.init(mc, o.getEdition(), o.getDescription());
						pricesPanel.revalidate();
						
				}, "loading prices for "+o.getDescription());
				
				btnDeleteOrder.setEnabled(true);
				btnSaveOrder.setEnabled(true);
				btnAddToCollection.setEnabled(true);
				}
				catch(Exception e)
				{
					btnDeleteOrder.setEnabled(false);
					

				}
			}
		});
		
		btnSave.addActionListener(ae->
			model.getItems().stream().filter(OrderEntry::isUpdated).forEach(o->{
					try {
						MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateOrderEntry(o);
						o.setUpdated(false);

					} catch (Exception e) {
						MTGControler.getInstance().notify(new MTGNotification("ERROR", e));
					}})
		);
		
		btnImportTransaction.addActionListener(ae->{
			OrderImporterDialog diag = new OrderImporterDialog();
			diag.setVisible(true);
			
			if(diag.getSelectedEntries()!=null) {
				model.addItems(diag.getSelectedEntries());
				calulate(model.getItems());
			}
		});
		
	}

	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("FINANCIAL_MODULE");
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	private void calulate(List<OrderEntry> entries)
	{
		double totalS=0;
		double totalB=0;

		for(OrderEntry e : entries)
		{
			if(e.getTypeTransaction().equals(TYPE_TRANSACTION.BUY))
				totalB=totalB+e.getItemPrice();
			else
				totalS=totalS+e.getItemPrice();
		}
	
		if(entries.size()<model.getRowCount())
		{
			selectionBuy.setText(UITools.formatDouble(totalB));
			selectionSell.setText(UITools.formatDouble(totalS));
			totalSelection.setText(": "+UITools.formatDouble(totalS-totalB)+")");
			if((totalS-totalB)>0)
				totalSelection.setIcon(MTGConstants.ICON_UP);
			else
				totalSelection.setIcon(MTGConstants.ICON_DOWN);
			
		}
		else
		{
			totalBuy.setText(UITools.formatDouble(totalB));
			totalSell.setText(UITools.formatDouble(totalS));
			total.setText(": "+UITools.formatDouble(totalS-totalB));
			
			if((totalS-totalB)>0)
				total.setIcon(MTGConstants.ICON_UP);
			else
				total.setIcon(MTGConstants.ICON_DOWN);
		}
	}
	

}
