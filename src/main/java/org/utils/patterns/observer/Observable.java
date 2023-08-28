package org.utils.patterns.observer;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

public class Observable {
	private transient boolean changed = false;
	private transient List<Observer> obs;
	protected static Logger logger = MTGLogger.getLogger(Observable.class);

	public Observable() {
		obs = new ArrayList<>();
	}

	public synchronized void addObserver(Observer o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			obs.add(o);
		}
	}

	public List<Observer> listObservers()
	{
		return obs;
	}


	public void notifyObservers(Object arg) {
		Object[] arrLocal;
		synchronized (this) {
			if (!changed)
				return;
			arrLocal = obs.toArray();
			clearChanged();
		}

		for (var i = arrLocal.length - 1; i >= 0; i--)
			((Observer) arrLocal[i]).update(this, arg);
	}

	public synchronized void setChanged() {
		changed = true;
	}

	public synchronized void clearChanged() {
		changed = false;
	}

	public synchronized boolean hasChanged() {
		return changed;
	}

	public synchronized void removeObserver(Observer o) {
		if (o == null)
			throw new NullPointerException();
		if (obs.contains(o)) {
			obs.remove(o);
		}
	}

	public synchronized void removeObservers() {
		obs.clear();
	}

}