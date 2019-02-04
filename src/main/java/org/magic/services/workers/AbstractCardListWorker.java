package org.magic.services.workers;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

@Deprecated
public abstract class AbstractCardListWorker extends SwingWorker<List<MagicCard>, MagicCard> {

	protected DefaultListModel<MagicCard> model;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Observer o;
	protected List<MagicCard> cards = null;
	protected AbstractBuzyIndicatorComponent buzy;
	
	public AbstractCardListWorker(DefaultListModel<MagicCard> model,AbstractBuzyIndicatorComponent buzy) {
		this.model=model;
		this.buzy=buzy;
		model.removeAllElements();
		o=(Observable obs, Object c)->publish((MagicCard)c);
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).addObserver(o);
	}
	
	@Override
	protected void process(List<MagicCard> chunks) {
		chunks.forEach(model::addElement);
		buzy.progressSmooth(chunks.size());
	}
	
	@Override
	protected void done() {
		try {
			MTGControler.getInstance().getEnabled(MTGCardsProvider.class).removeObserver(o);
		} catch (Exception e) {
			logger.error(e);
		}
		buzy.end();
	}
	
}
