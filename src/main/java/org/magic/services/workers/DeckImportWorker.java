package org.magic.services.workers;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class DeckImportWorker extends SwingWorker<MTGDeck, MTGCard> {

	protected MTGCardsExport exp;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Observer o;
	protected List<MTGCard> cards = null;
	protected AbstractBuzyIndicatorComponent buzy;
	private File f;
	protected Exception err;

	public DeckImportWorker(MTGCardsExport exp,AbstractBuzyIndicatorComponent buzy,File f) {
		this.exp=exp;
		this.buzy=buzy;
		this.f=f;
		err=null;
		o=(Observable _, Object c)->publish((MTGCard)c);
		exp.addObserver(o);
	}



	@Override
	protected MTGDeck doInBackground(){
		try {
			return exp.importDeckFromFile(f);
		} catch (Exception e) {
			err=e;
			logger.error("error export with {}", exp,e);
		}
		return null;
	}

	@Override
	protected void process(List<MTGCard> chunks) {
		chunks.forEach(cs->{
			buzy.setText(cs.toString());
			buzy.progress();
		});
	}

	@Override
	protected void done() {
		try {
			exp.removeObserver(o);
		} catch (Exception e) {
			logger.error(e);
		}
		buzy.end();

		if(err!=null)
		{
			MTGControler.getInstance().notify(err);
		}
		else
		{
			MTGControler.getInstance().notify(new MTGNotification(
					exp.getName() + " "+ MTGControler.getInstance().getLangService().get("FINISHED"),
					MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
					MESSAGE_TYPE.INFO
					));
		}

	}

}
