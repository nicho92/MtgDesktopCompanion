package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.technical.audit.JsonQueryInfo;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.network.URLTools;

public class JsonInfoTableModel extends GenericTableModel<JsonQueryInfo> {

	private static final long serialVersionUID = 1L;


	public JsonInfoTableModel() {
		setColumns("method","url","start","end","duration","status","contentType","ip","source");
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

		if(column==8)
			return items.get(row).getHeaders().get(URLTools.ORIGIN);

		return super.getValueAt(row, column);
	}



}
