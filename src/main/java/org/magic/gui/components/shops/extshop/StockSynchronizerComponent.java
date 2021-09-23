package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.StockItemTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;


public class StockSynchronizerComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGExternalShop> cboInput;
	private JComboBox<MTGExternalShop> cboOutput;
	
	private JXTable listInput;
	private StockItemTableModel modelInput;
	
	private JXTable listOutput;
	private StockItemTableModel modelOutput;
	
	private AbstractBuzyIndicatorComponent buzy;
	private JPanel panel;

	
	public StockSynchronizerComponent() {
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		var btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24, KeyEvent.VK_F,"searchProduct");
		
		var panelNorth = new JPanel();
		var panelWest = new JPanel();
		panelWest.setLayout(new BorderLayout());
		var panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());
		
		cboInput = UITools.createCombobox(MTGExternalShop.class,true);
		cboOutput= UITools.createCombobox(MTGExternalShop.class,true);
	
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		modelInput = new StockItemTableModel();

		listInput = UITools.createNewTable(modelInput);
		modelOutput= new StockItemTableModel();
		listOutput = UITools.createNewTable(modelOutput);
		
		
		for(var i : new int[] {2,4,5,8,9})
		{
			listInput.getColumnExt(modelInput.getColumnName(i)).setVisible(false);
			listOutput.getColumnExt(modelInput.getColumnName(i)).setVisible(false);
		}
		
		panelNorth.add(btnSearch);
		panelNorth.add(buzy);
		
		add(panelNorth, BorderLayout.NORTH);
		add(panelWest,BorderLayout.WEST);
		add(panelEast,BorderLayout.EAST);
		
		panelWest.add(cboInput, BorderLayout.NORTH);
		panelEast.add(cboOutput, BorderLayout.NORTH);
		
		panelWest.add(new JScrollPane(listInput), BorderLayout.CENTER);
		panelEast.add(new JScrollPane(listOutput), BorderLayout.CENTER);
		
		
		add(panel, BorderLayout.CENTER);
		btnSearch.addActionListener(e->loadProducts());
		
		
		cboOutput.addItemListener(il->{
			 if (il.getStateChange() == ItemEvent.SELECTED) {
					
		       }
		});
		
		
	}

	private void loadProducts() {
		
		modelInput.clear();
		
		AbstractObservableWorker<List<MTGStockItem>,MTGStockItem,MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,(MTGExternalShop)cboInput.getSelectedItem())
		{
			@Override
			protected List<MTGStockItem> doInBackground() throws Exception {
					return plug.listStock();
			}
			
			@Override
			protected void done() {
				try {
					super.done();
					modelInput.addItems(get());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}
			}
			
		};
		
		ThreadManager.getInstance().runInEdt(sw,"load stock");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_STOCK;
	}

	@Override
	public String getTitle() {
		return "STOCK SYCHRONISATION";
	}

}
