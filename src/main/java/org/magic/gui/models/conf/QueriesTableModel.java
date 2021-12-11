package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.DAOInfo;
import org.magic.gui.abstracts.GenericTableModel;

public class QueriesTableModel extends GenericTableModel<DAOInfo> {

	private static final long serialVersionUID = 1L;

	public QueriesTableModel() {
		setColumns("query","creationDate","endDate","duration","message");
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==1 || columnIndex==2)
			return Instant.class;
		
		if(columnIndex==3)
			return Long.class;
		
		
		return super.getColumnClass(columnIndex);
	}
	
	
}
