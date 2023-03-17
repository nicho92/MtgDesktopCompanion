package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.SealedStock;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.dialog.SealedImportDialog;
import org.magic.gui.models.StockItemTableModel;
import org.magic.gui.renderer.StockTableRenderer;
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
		table = UITools.createNewTable(model);
		panneauHaut = new JPanel();
		
		UITools.setDefaultRenderer(table, new StockTableRenderer());


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
		
		btnAddSealed.addActionListener(al->{
			
			var diag = new SealedImportDialog();
			diag.setVisible(true);
			
			if(diag.getSelecteds().isEmpty())
				return;
			
			
			for(var it : diag.getSelecteds())
			{
	   		var mtgstock = new SealedStock();
					mtgstock.setProduct(it);
					model.addItem(mtgstock);
			}
			model.fireTableDataChanged();
		});
		
		
		
		btnAddCard.addActionListener(al->{
			var cdSearch = new CardSearchImportDialog();
				 cdSearch.setVisible(true);
			if (cdSearch.getSelection() != null) {
				for (var mc : cdSearch.getSelection())
				{
					var mtgstock = MTGControler.getInstance().getDefaultStock();
        			mtgstock.setProduct(mc);
        			mtgstock.setQte(1);
        			model.addItem(mtgstock);
        			model.fireTableDataChanged();
				}
			}
		});
		
		btnRemoveProduct.addActionListener(al->{
			MTGStockItem selection = UITools.getTableSelection(table, 0);
			model.removeItem(selection);
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
			model.init(st);
			table.packAll();
		} catch (Exception e) {
			logger.error(e);
		}

	}


	public void bind(List<MTGStockItem> items) {

		try {
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


	public void refresh() {
		model.fireTableDataChanged();
		
	}


}
