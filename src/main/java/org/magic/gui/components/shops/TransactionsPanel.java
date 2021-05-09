package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.models.TransactionsModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionsPanel extends MTGUIComponent {
	private JXTable table;
	private TransactionsModel model;
	
	
	public TransactionsPanel() {
		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		var panneauBas = new CardStockPanel();
		model = new TransactionsModel();
		
		
		var btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		table = UITools.createNewTable(model);
		table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		UITools.initTableFilter(table);
		
		
		table.packAll();
		panneauBas.showAllColumns();
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		add(panneauBas,BorderLayout.SOUTH);
		panneauHaut.add(btnRefresh);
		
		table.getSelectionModel().addListSelectionListener(lsl->{
			
			Transaction t = UITools.getTableSelection(table, 0);
			
			if(t==null)
				return;
			
			panneauBas.initMagicCardStock(t.getItems());
			panneauBas.disableCommands();
		});
		
		btnRefresh.addActionListener(al->reload());
		
	}
	
	private void reload()
	{
		try {
			model.clear();
			model.addItems(MTG.getEnabledPlugin(MTGDao.class).listTransactions());
			model.fireTableDataChanged();
		} catch (Exception e) {
			logger.error("error loading transactions",e);
		}
	}
	
	

	@Override
	public void onFirstShowing() {
		reload();
		
	}
	
	@Override
	public String getTitle() {
		return "Transaction";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_EURO;
	}

	
}
