package org.magic.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.magic.api.main.MtgDesktopCompanion;

public class MTGAppender extends AppenderSkeleton {

	
	List<LoggingEvent> events;
	
	public MTGAppender() {
		events=new ArrayList<LoggingEvent>();
	}
	
	
	@Override
	public void close() {
		

	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		events.add(event);
		if(MtgDesktopCompanion.launch!=null)
			MtgDesktopCompanion.launch.update(event.getMessage().toString());
	}


}
