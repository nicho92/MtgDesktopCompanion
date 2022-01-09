package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.audit.JsonQueryInfo;
import org.magic.gui.abstracts.GenericTableModel;

public class JsonInfoTableModel extends GenericTableModel<JsonQueryInfo> {

	private static final long serialVersionUID = 1L;

	
	public JsonInfoTableModel() {
		setColumns("method","url","start","end","duration","status","contentType","ip");
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==2 || columnIndex==3)
			return Instant.class;
		
		if(columnIndex==4)
			return Long.class;
		
		
		return super.getColumnClass(columnIndex);
	}
	
	
	
}
