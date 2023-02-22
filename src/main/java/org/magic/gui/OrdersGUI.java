package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.OrderImporterDialog;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.gui.renderer.OrderEntryRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
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
	private AbstractBuzyIndicatorComponent buzy;

	private void loadFinancialBook()
	{
		model.clear();
		SwingWorker<List<OrderEntry>, OrderEntry> sw = new SwingWorker<>()
				{

					@Override
					protected List<OrderEntry> doInBackground() throws Exception {
						return getEnabledPlugin(MTGDao.class).listOrders();
					}

					@Override
					protected void done() {
						try {
							model.addItems(get());
							calulate(model.getItems());
						}catch(InterruptedException ex)
						{
							Thread.currentThread().interrupt();
						}
						catch (Exception e) {
							logger.error(e);
						}
						table.packAll();

					}

				};

			ThreadManager.getInstance().runInEdt(sw,"loading orders");
	}

	@Override
	public void onFirstShowing() {
		loadFinancialBook();
	}


	public OrdersGUI() {

		var panneauBas = new JPanel();
		var panneauHaut = new JPanel();

		model = new ShoppingEntryTableModel();
		table = UITools.createNewTable(model);
		UITools.initTableFilter(table);

		var btnImportTransaction = UITools.createBindableJButton(null,MTGConstants.ICON_IMPORT,KeyEvent.VK_I,"transaction import");
		var btnSave = UITools.createBindableJButton(null,MTGConstants.ICON_SAVE,KeyEvent.VK_S,"transactions save");
		totalBuy = new JLabel(MTGConstants.ICON_DOWN);
		totalSell = new JLabel(MTGConstants.ICON_UP);
		total = new JLabel();
		totalSelection = new JLabel();
		selectionSell = new JLabel(MTGConstants.ICON_UP);
		selectionBuy=new JLabel(MTGConstants.ICON_DOWN);
	

	
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();

		setLayout(new BorderLayout(0, 0));

		table.setDefaultRenderer(Double.class,  new OrderEntryRenderer());
	
		panneauBas.add(totalBuy);
		panneauBas.add(totalSell);
		panneauBas.add(total);
		panneauBas.add(new JLabel(" ("));
		panneauBas.add(selectionBuy);
		panneauBas.add(selectionSell);
		panneauBas.add(totalSelection);
		
		panneauHaut.add(new JLabel("THIS MODULE WILL BE REMOVED. USING Transaction Module Instead"));
		panneauHaut.add(btnImportTransaction);
		panneauHaut.add(btnSave);
		panneauHaut.add(buzy);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	

		add(panneauBas,BorderLayout.SOUTH);

		table.setSortOrder(2, SortOrder.DESCENDING);



		
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {

					if(table.getSelectedRow()<0)
						return;

				calulate(UITools.getTableSelections(table, 0));

			}
		});


		btnImportTransaction.addActionListener(ae->{
			var diag = new OrderImporterDialog();
			diag.setVisible(true);

			if(diag.getSelectedEntries()!=null) {
				model.addItems(diag.getSelectedEntries());
				calulate(model.getItems());
			}
		});

	}

	@Override
	public String getTitle() {
		return capitalize("FINANCIAL_MODULE");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_EURO;
	}

	private void calulate(List<OrderEntry> entries)
	{
		double totalS=0;
		double totalB=0;

		for(OrderEntry e : entries)
		{
			if(e.getTypeTransaction().equals(TransactionDirection.BUY))
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
