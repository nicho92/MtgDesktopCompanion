package org.magic.gui.components.dialog.importer;

import java.util.List;

import javax.swing.JComponent;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.card.CardStockPanel;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.workers.AbstractObservableWorker;

public class CardStockChooseDialog extends AbstractDelegatedImporterDialog<MTGCardStock> {

	private static final long serialVersionUID = 1L;
	CardStockPanel panel;
	AbstractBuzyIndicatorComponent buzy;
	
	
	public CardStockChooseDialog() {
		
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		var sw2 = new AbstractObservableWorker<List<MTGCardStock>, MTGCardStock, MTGDao>(buzy,MTG.getEnabledPlugin(MTGDao.class))
		{

			@Override
			protected List<MTGCardStock> doInBackground() throws Exception {
				return plug.listStocks();
			}
			
			@Override
			protected void done() {
				super.done();
				panel.initMagicCardStock(getResult());
			}
		};
		
		commandePanel.add(buzy);
		
		ThreadManager.getInstance().runInEdt(sw2,"loading contacts");
	}
	
	@Override
	public MTGCardStock getSelectedItem() {
		return panel.getSelected();
	}
	
	@Override
	public List<MTGCardStock> getSelectedItems() {
		return panel.getMultiSelection();
	}
	
	@Override
	public JComponent getSelectComponent() {
		panel = new CardStockPanel();
		panel.disableCommands();
		return panel;
	}

}
