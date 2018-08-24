package org.magic.gui.models;

import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.magic.services.MTGAppender;
import org.magic.services.MTGLogger;

public class LogTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient MTGAppender app;
	private static final String[] COLUMNS = { "LEVEL", "TIME", "CLASS", "MESSAGE" };

	public LogTableModel() {
		app = (MTGAppender) MTGLogger.getAppender("APPS");
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
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public int getRowCount() {
		if (app != null)
			return app.getEvents().size();
		return 0;
	}

}
