package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.audit.NetworkInfo;
import org.magic.gui.abstracts.GenericTableModel;

public class NetworkTableModel extends GenericTableModel<NetworkInfo> {

	private static final long serialVersionUID = 1L;

	
	public NetworkTableModel() {
		setColumns("method","url","start","end","duration","response","code","server","content Type");
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
	
	
	@Override
	public Object getValueAt(int row, int column) {
		
		switch(column)
		{
			case 0 : return items.get(row).getRequest().getMethod();
			case 1 : return items.get(row).getRequest().getURI();
			case 5 : return items.get(row).getResponse().getStatusLine().getReasonPhrase();
			case 6 : return items.get(row).getResponse().getStatusLine().getStatusCode();
			case 7 : return items.get(row).getServer();
			case 8 : return items.get(row).getContentType();
			default : return super.getValueAt(row, column);
		}
		
	}
	
	
}
