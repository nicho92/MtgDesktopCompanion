package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.importer.CardStockChooseDialog;
import org.magic.gui.components.dialog.importer.SealedStockChooseDialog;
import org.magic.gui.models.StockItemTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.UITools;

import com.jogamp.newt.event.KeyEvent;


public class StockItemPanel extends MTGUIComponent {

	
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private StockItemTableModel model;
	private JPanel panneauHaut;
	
	
	public StockItemPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new StockItemTableModel();
		table = UITools.createNewTable(model,false);
		panneauHaut = new JPanel();
		

		var btnAddSealed = UITools.createBindableJButton("", MTGConstants.ICON_PACKAGE,KeyEvent.VK_S,"sealed");
		var btnAddCard = UITools.createBindableJButton("", MTGConstants.ICON_NEW,KeyEvent.VK_C,"card");
		var btnRemoveProduct = UITools.createBindableJButton("", MTGConstants.ICON_DELETE,KeyEvent.VK_D,"remove");
		
		panneauHaut.add(btnAddCard);
		panneauHaut.add(btnAddSealed);
		panneauHaut.add(btnRemoveProduct);
		

		UITools.initTableVisibility(table,model);

		
		add(new JScrollPane(table),BorderLayout.CENTER);
		add(panneauHaut,BorderLayout.NORTH);

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				MTGStockItem selection = UITools.getTableSelection(table, 0);

				if(selection==null)
					return;
			}
		});
		
		btnAddSealed.addActionListener(_->{
			
			var diag = new SealedStockChooseDialog();
			diag.setVisible(true);
			
			if(!diag.hasSelected())
				return;
			
			
			for(var it : diag.getSelectedItems())
			{
	   				model.addItem(it);
			}
			model.fireTableDataChanged();
		});
		
		
		
		btnAddCard.addActionListener(_->{
			var cdSearch = new CardStockChooseDialog();
				 cdSearch.setVisible(true);
			if (cdSearch.hasSelected()) {
				for (var mc : cdSearch.getSelectedItems())
        			model.addItem(mc);
				
    			model.fireTableDataChanged();

			}
		});
		
		btnRemoveProduct.addActionListener(_->{
			var selection = UITools.getSelectedRows(table);
			model.removeRows(selection);
			model.fireTableDataChanged();
			
		});
		
		
		
	}
	
	public void setWritable(boolean b)
	{
		panneauHaut.setVisible(b);
		model.setWritable(b);
	}

	
	@Override
	public void onHide() {
		boolean isUpdatedModel = model.getItems().stream().anyMatch(MTGStockItem::isUpdated);

		if(isUpdatedModel)
		{
			MTGControler.getInstance().notify(new MTGNotification("Item Updated", "Don't forget to save your updates", MESSAGE_TYPE.WARNING));
		}

	}


	public void initItems(List<MTGStockItem> st) {

		try {
			if(st!=null && !st.isEmpty())
				model.init(st);
			
			table.packAll();
		} catch (Exception e) {
			logger.error(e);
		}

	}


	public void bind(List<MTGStockItem> items) {
		try {
			
			if(items!=null && !items.isEmpty())
				model.bind(items);
			
			table.packAll();
		} catch (Exception e) {
			logger.error(e);
		}
		
	}


	


	@Override
	public String getTitle() {
		return "Products";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_STOCK;
	}


	public List<MTGStockItem> getItems() {
		return model.getItems();
	}

}
