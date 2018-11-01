package org.magic.gui.models;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import org.magic.gui.abstracts.GenericTableModel;

public class ThreadsTableModel extends GenericTableModel<ThreadInfo> {

	public ThreadsTableModel() {
		columns = new String[] {"ID","NAME","CPU (s.)","STATE","PRIORITY","LOCK"};
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
				return ManagementFactory.getThreadMXBean().getThreadCpuTime(t.getThreadId())/(1e+9);
			case 3:
				return t.getThreadState().name();
			case 4:
				return t.getPriority();
			case 5:
				return t.getLockInfo();
			default: 
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}
	
	
}
