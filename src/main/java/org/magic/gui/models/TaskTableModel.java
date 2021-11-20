package org.magic.gui.models;

import java.time.Instant;

import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.threads.ThreadInfo;

public class TaskTableModel extends GenericTableModel<ThreadInfo> {

	private static final long serialVersionUID = 1L;

	
	public TaskTableModel() {
		setColumns("name","createdDate","startDate","endDate","status","type","duration");
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==1 || columnIndex==2)
			return Instant.class;
		
		if(columnIndex==5)
			return Long.class;
		
		
		return super.getColumnClass(columnIndex);
	}
	
}
