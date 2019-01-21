package org.magic.services.workers;

import java.util.List;

import javax.swing.SwingWorker;

import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;

public abstract class AbstractProgressWorker<T, V> extends SwingWorker<T, V> {

	
	private AbstractBuzyIndicatorComponent buzy;


	@Override
	protected void process(List<V> chunks) {
		buzy.progressSmooth(chunks.size());
	}
	
	
	public AbstractProgressWorker(AbstractBuzyIndicatorComponent buzy) {
		this.buzy=buzy;
	}
	
	
	@Override
	protected void done() {
		buzy.end();
	}

}
