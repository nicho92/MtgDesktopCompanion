package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.messages.TalkMessage;
import org.magic.gui.abstracts.GenericTableModel;

public class ActiveMQMessageTableModel extends GenericTableModel<TalkMessage> {

	private static final long serialVersionUID = 1L;

	public ActiveMQMessageTableModel() {
		setColumns("id","start","end","duration","author.id","author.name","message","typeMessage");
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
