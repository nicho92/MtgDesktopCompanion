package org.magic.services.workers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.api.sorters.PricesCardsShakeSorter;
import org.magic.api.sorters.PricesCardsShakeSorter.SORT;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.CollectionAnalyzerTreeTableModel;
import org.magic.gui.models.MapTableModel;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.tools.UITools;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class CollectionAnalyzerWorker extends SwingWorker<Void, MagicEdition> {

	protected CollectionEvaluator evaluator;
	protected MapTableModel<MagicEdition, Date> cacheModel;
	protected CollectionAnalyzerTreeTableModel collectionModel;
	protected JXTreeTable treeTable;
	protected Observer o;
	protected AbstractBuzyIndicatorComponent buzy;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected List<MagicEdition> eds;
	protected JLabel lblPrice;
	
	public CollectionAnalyzerWorker(CollectionEvaluator evaluator, JXTreeTable treeTable, MapTableModel<MagicEdition, Date> modelCache, AbstractBuzyIndicatorComponent buzy,JLabel labTotal) {
		this.treeTable=treeTable;
		this.cacheModel=modelCache;
		collectionModel = new CollectionAnalyzerTreeTableModel();
		this.evaluator=evaluator;
		this.buzy=buzy;
		this.lblPrice=labTotal;
		
		o=(Observable obs, Object ed)->publish((MagicEdition)ed);
		eds = evaluator.getEditions();
		buzy.start(eds.size());
		evaluator.addObserver(o);
		cacheModel.clear();
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		Collections.sort(eds);
		for(MagicEdition ed : eds)
		{
			cacheModel.addRow(ed, evaluator.getCacheDate(ed));
			List<CardShake> list = new ArrayList<>(evaluator.prices(ed).values());
			AbstractDashBoard.convert(list);
			Collections.sort(list,new PricesCardsShakeSorter(SORT.PRICE,false));
			collectionModel.saveRow(ed,list);
		}
		return null;
	}
	
	@Override
	protected void process(List<MagicEdition> chunks) {
		buzy.progressSmooth(chunks.size());
		cacheModel.fireTableDataChanged();
	}
	
	@Override
	protected void done() {
		
		
		try {
			get();
		} catch (InterruptedException e) {
			logger.error("Interruption",e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.error(e);
		}
		catch (CancellationException e) {
			logger.warn("SwingWorker canceled");
		}
		
		treeTable.setTreeTableModel(collectionModel);
		evaluator.removeObserver(o);
		Double total = evaluator.total();
		buzy.end();
		lblPrice.setText("Value : " + UITools.formatDouble(total) + " " + MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode());
	}
	
}


