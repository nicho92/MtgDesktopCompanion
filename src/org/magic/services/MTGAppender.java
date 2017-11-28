package org.magic.services;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.magic.api.main.MtgDesktopCompanion;

public class MTGAppender extends AppenderSkeleton {

	@Override
	public void close() {
		

	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		if(MtgDesktopCompanion.launch!=null)
			MtgDesktopCompanion.launch.update(event.getMessage().toString());
	}


}
