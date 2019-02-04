package org.magic.services.workers;

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class AbstractObservableWorker<T, V, P extends MTGPlugin> extends SwingWorker<T, V> {

	protected AbstractBuzyIndicatorComponent buzy;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Observer o;
	protected P plug;

	public AbstractObservableWorker(AbstractBuzyIndicatorComponent buzy,P plug,int size) {
		this.buzy=buzy;
		this.plug=plug;
		o=(Observable obs, Object c)->publish((V)c);
		plug.addObserver(o);
		
		if(size>-1)
			buzy.start(size);
		else
			buzy.start();
	}
	
	@Override
	protected void process(List<V> chunks) {
		buzy.progressSmooth(chunks.size());
	}
	
	@Override
	protected void done() {
		buzy.end();
		
		try {
			get();
			notifyEnd();
		}
		catch(Exception e)
		{
			error(e);
		}
		
		
	}

	protected void notifyEnd() {
		
	}
	
	protected void error(Exception e)
	{
		MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
	}
}
