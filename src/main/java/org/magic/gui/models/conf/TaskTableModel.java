package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.audit.TaskInfo;
import org.magic.gui.abstracts.GenericTableModel;

public class TaskTableModel extends GenericTableModel<TaskInfo> {

	private static final long serialVersionUID = 1L;

	
	public TaskTableModel() {
		setColumns("name","created","start","end","status","type","duration");
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==1 || columnIndex==2|| columnIndex==3)
			return Instant.class;
		
		if(columnIndex==6)
			return Long.class;
		
		
		return super.getColumnClass(columnIndex);
	}
	
}
