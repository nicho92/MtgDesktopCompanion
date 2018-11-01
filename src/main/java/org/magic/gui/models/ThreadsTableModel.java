package org.magic.gui.models;

import java.lang.management.ThreadInfo;

import org.magic.gui.abstracts.GenericTableModel;

public class ThreadsTableModel extends GenericTableModel<ThreadInfo> {

	public ThreadsTableModel() {
		columns = new String[] {"ID","NAME","STATE","PRIORITY","LOCK"};
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		ThreadInfo t = items.get(row);
		try {
			switch (column) {
			case 0:
				return t.getThreadId();
			case 1:
				return t.getThreadName();
			case 2:
				return t.getThreadState();
			case 3:
				return t.getPriority();
			case 4:
				return t.getLockInfo();
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}
	
	
}
