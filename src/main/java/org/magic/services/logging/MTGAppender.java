package org.magic.services.logging;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

@Plugin(name = "MTGAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class MTGAppender extends AbstractAppender  {

	private List<LogEvent> events;
	private Observable obs;

	public List<LogEvent> getEvents() {
		return events;
	}

	public Observable getObservable() {
		return obs;
	}

	public void addObserver(Observer viewer) {
		obs.addObserver(viewer);
	}


	@PluginFactory
    public static MTGAppender createAppender(@PluginAttribute("name") String name,@PluginElement("Filter") Filter filter) {
        return new MTGAppender(name, filter);
    }

	public MTGAppender(String name, Filter filter) {
		super(name,filter,null,false,null);
		events = new ArrayList<>();
		obs = new Observable();
	}



	@Override
	public void append(LogEvent event) {
		events.add(event);
		obs.setChanged();
		try {
			obs.notifyObservers(event.getMessage());
		} catch (Exception _) {
			// do nothing
		}
	}

}
