package org.magic.gui.models.conf;

import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.services.MTGAppender;

public class LogTableModel extends DefaultTableModel {

	MTGAppender app ;
	
	static final String[] COLUMNS= {"LEVEL","TIME","CLASS","MESSAGE"};
	
	public LogTableModel() {
		app = (MTGAppender)Logger.getRootLogger().getAppender("APPS");
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return app.getEvents().get(row).getLevel();
		
		if(column==1)
			return new Date(app.getEvents().get(row).getTimeStamp());
		
		if(column==2)
			return app.getEvents().get(row).getLocationInformation().getClassName();
		
		
		if(column==3)
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
		if(app!=null)
			return app.getEvents().size();
		return 0;
	}
	
}
