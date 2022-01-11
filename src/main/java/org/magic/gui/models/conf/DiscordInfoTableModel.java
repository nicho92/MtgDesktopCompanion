package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.audit.DAOInfo;
import org.magic.api.beans.audit.DiscordInfo;
import org.magic.gui.abstracts.GenericTableModel;

public class DiscordInfoTableModel extends GenericTableModel<DiscordInfo> {

	private static final long serialVersionUID = 1L;

	public DiscordInfoTableModel() {
		setColumns("start","end","duration","message","user","guild","channel");
		setWritable(false);
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==0 || columnIndex==1)
			return Instant.class;
		
		if(columnIndex==2)
			return Long.class;
		
		
		return super.getColumnClass(columnIndex);
	}
	
	
}
