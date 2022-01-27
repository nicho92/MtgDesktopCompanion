package org.magic.gui.models.conf;

import java.time.Instant;

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
	
	@Override
	public Object getValueAt(int row, int column) {
		
		if(column>3)
		{
			switch(column)
			{
			case 4: return (items.get(row).getUser()!=null)?items.get(row).getUser().get("name").getAsString():"";
			case 5: return (items.get(row).getGuild()!=null)?items.get(row).getGuild().get("name").getAsString():"";
			case 6: return (items.get(row).getChannel()!=null)?items.get(row).getChannel().get("name").getAsString():"";
			}
		}
		
		
		return super.getValueAt(row, column);
	}
	
	
}
