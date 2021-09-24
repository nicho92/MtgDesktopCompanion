package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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
	
	private JXTable tableInput;
	private StockItemTableModel modelInput;
	
	private JXTable tableOutput;
	private StockItemTableModel modelOutput;
	
	private AbstractBuzyIndicatorComponent buzy;
	
	public StockSynchronizerComponent() {
		setLayout(new BorderLayout(0, 0));

		var panelCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		var panelNorth = new JPanel();
		var panelWest = new JPanel();
		panelWest.setLayout(new BorderLayout());
		var panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());
		
		cboInput = UITools.createCombobox(MTGExternalShop.class,true);
		cboOutput= UITools.createCombobox(MTGExternalShop.class,true);
	
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		modelInput = new StockItemTableModel();

		tableInput = UITools.createNewTable(modelInput);
		modelOutput= new StockItemTableModel();
		tableOutput = UITools.createNewTable(modelOutput);
		
		
		for(var i : new int[] {2,4,5,8,9})
		{
			tableInput.getColumnExt(modelInput.getColumnName(i)).setVisible(false);
			tableOutput.getColumnExt(modelInput.getColumnName(i)).setVisible(false);
		}
		panelNorth.add(buzy);
		
		add(panelNorth, BorderLayout.NORTH);
		add(panelCenter,BorderLayout.CENTER);
		
		panelCenter.setLeftComponent(panelWest);
		panelCenter.setRightComponent(panelEast);
		
		panelWest.add(cboInput, BorderLayout.NORTH);
		panelWest.add(new JScrollPane(tableInput), BorderLayout.CENTER);
		
		panelEast.add(cboOutput, BorderLayout.NORTH);
		panelEast.add(new JScrollPane(tableOutput), BorderLayout.CENTER);
	
		
		cboInput.addItemListener(il->{
			 if (il.getStateChange() == ItemEvent.SELECTED) {
				 
					loadProducts((MTGExternalShop)cboInput.getSelectedItem(),modelInput,0);
		       }
		});
		
		cboOutput.addItemListener(il->{
			 if (il.getStateChange() == ItemEvent.SELECTED) {
				 loadProducts((MTGExternalShop)cboOutput.getSelectedItem(),modelOutput,0);
		       }
		});
		
		
	}

	private void loadProducts(MTGExternalShop ext,StockItemTableModel model,int start) {
		
		if(start<=0)
			model.clear();
		
		
		AbstractObservableWorker<List<MTGStockItem>,MTGStockItem,MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,ext)
		{
			@Override
			protected List<MTGStockItem> doInBackground() throws Exception {
					return plug.listStock(start);
			}
			
			@Override
			protected void done() {
				try {
					super.done();
					model.addItems(get());
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
