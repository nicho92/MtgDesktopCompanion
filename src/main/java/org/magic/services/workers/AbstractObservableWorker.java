package org.magic.services.workers;

import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class AbstractObservableWorker<T, V, P extends MTGPlugin> extends SwingWorker<T, V> {

	protected AbstractBuzyIndicatorComponent buzy;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected Observer o;
	protected P plug;

	public T getResult()
	{
		try {
			return get();
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
			return null;
		}
		catch (Exception e) {
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	protected AbstractObservableWorker(P plug) {
		this.plug=plug;
		o=(Observable obs, Object c)->publish((V)c);
		plug.addObserver(o);
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
	}


	@SuppressWarnings("unchecked")
	protected AbstractObservableWorker(AbstractBuzyIndicatorComponent buzy,P plug,Integer size) {
		this.buzy=buzy;
		this.plug=plug;
		o=(Observable obs, Object c)->publish((V)c);
		plug.addObserver(o);

		if(size>-1)
			buzy.start(size);
		else
			buzy.start();
	}

	@SuppressWarnings("unchecked")
	protected AbstractObservableWorker(AbstractBuzyIndicatorComponent buzy,P plug) {
		this.buzy=buzy;
		this.plug=plug;
		o=(Observable obs, Object c)->publish((V)c);
		plug.addObserver(o);
		buzy.start();
	}


	@Override
	protected void process(List<V> chunks) {
		buzy.progressSmooth(chunks.size());
	}

	@Override
	protected void done() {
		buzy.end();
		plug.removeObserver(o);
		try {
			get();

		}
		catch(InterruptedException | CancellationException ex)
		{
			Thread.currentThread().interrupt();
		}
		catch(Exception e)
		{
			error(e);
		}
		notifyEnd();

	}

	protected void notifyEnd() {
		//do nothing by default
	}

	protected void error(Exception e)
	{
		logger.error("error", e);
		MTGControler.getInstance().notify(e);
	}
}
