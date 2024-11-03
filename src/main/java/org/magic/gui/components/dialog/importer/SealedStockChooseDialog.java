package org.magic.gui.components.dialog.importer;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.models.SealedStockTableModel;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class SealedStockChooseDialog extends AbstractDelegatedImporterDialog<MTGSealedStock> {

	private static final long serialVersionUID = 1L;
	private JXTable table;

	@Override
	public JComponent getSelectComponent() {
		var model = new SealedStockTableModel();
		table = UITools.createNewTable(model, true);
		var buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		
		var sw2 = new AbstractObservableWorker<List<MTGSealedStock>, MTGSealedStock, MTGDao>(buzy,MTG.getEnabledPlugin(MTGDao.class))
		{

			@Override
			protected List<MTGSealedStock> doInBackground() throws Exception {
				return plug.listSealedStocks();
			}
			
			@Override
			protected void done() {
				super.done();
				model.init(getResult());
			}
		};
		
		commandePanel.add(buzy);
		
		ThreadManager.getInstance().runInEdt(sw2,"loading sealed stocks");
	
		
		
		return new JScrollPane(table);
	}
	@Override
	public List<MTGSealedStock> getSelectedItems() {
		return UITools.getTableSelections(table, 0);
	}

}
