package org.magic.gui.models;

import java.util.Date;

import org.apache.log4j.spi.LoggingEvent;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGAppender;
import org.magic.services.MTGLogger;

public class LogTableModel extends GenericTableModel<LoggingEvent> {

	private static final long serialVersionUID = 1L;
	private transient MTGAppender app;
	
	public LogTableModel() {
		app = (MTGAppender) MTGLogger.getAppender("APPS");
		columns=new String[]{ "LEVEL", "TIME", "CLASS", "MESSAGE" };
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0)
			return app.getEvents().get(row).getLevel();

		if (column == 1)
			return new Date(app.getEvents().get(row).getTimeStamp());

		if (column == 2)
			return app.getEvents().get(row).getLocationInformation().getClassName();

		if (column == 3)
			return app.getEvents().get(row).getMessage();

		return "";
	}

	@Override
	public int getRowCount() {
		if (app != null)
			return app.getEvents().size();
		
		return 0;
	}

}
