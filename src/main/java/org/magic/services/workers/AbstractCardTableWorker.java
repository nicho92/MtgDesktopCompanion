package org.magic.services.workers;

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class AbstractCardTableWorker extends SwingWorker<List<MagicCard>, MagicCard> {

	protected AbstractBuzyIndicatorComponent buzy;
	protected GenericTableModel<MagicCard> model;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Observer o;
	protected List<MagicCard> cards = null;
	
	public AbstractCardTableWorker(GenericTableModel<MagicCard> model, AbstractBuzyIndicatorComponent buzy) {
		this.buzy=buzy;
		this.model=model;
		model.clear();
		o=(Observable obs, Object c)->publish((MagicCard)c);
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).addObserver(o);
	}
	
	@Override
	protected void process(List<MagicCard> chunks) {
		model.addItems(chunks);
		buzy.progressSmooth(chunks.size());
	}
	
	@Override
	protected void done() {
		
		try {
			model.init(get());
			MTGControler.getInstance().getEnabled(MTGCardsProvider.class).removeObserver(o);
		} catch (Exception e) {
			logger.error(e);
		}
		buzy.end();
	}
	
}
