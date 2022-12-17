package org.magic.gui.models.conf;

import java.time.Instant;

import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.gui.abstracts.GenericTableModel;

public class FileAccessTableModel extends GenericTableModel<FileAccessInfo> {

	private static final long serialVersionUID = 1L;

	public FileAccessTableModel() {
		setColumns("file","start","end","duration","accesstype");
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
