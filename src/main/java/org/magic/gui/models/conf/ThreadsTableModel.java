package org.magic.gui.models.conf;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;

import org.magic.gui.abstracts.GenericTableModel;

public class ThreadsTableModel extends GenericTableModel<ThreadInfo> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ThreadsTableModel() {
		columns = new String[] {"ID","PROCESS","CPU (s.)","STATE","PRIORITY","LOCK INFO","BLOCKED COUNT","DEADLOCKED","MEMORY (KB.)"};
	}



	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex)
		{
			case 0 : return Long.class;
			case 1 : return String.class;
			case 2 : return Double.class;
			case 3 : return String.class;
			case 4 : return Integer.class;
			case 5 : return String.class;
			case 6 : return Long.class;
			case 7 : return Boolean.class;
			case 8 : return Double.class;
			default : return Object.class;
		}
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
				return 1;
			case 5:
				return t.getLockOwnerName();
			case 6:
				return t.getBlockedCount();
			case 7:
				return Arrays.asList(ManagementFactory.getThreadMXBean().findDeadlockedThreads()).contains(t.getThreadId());
			case 8 :
				return ((com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean()).getThreadAllocatedBytes(t.getThreadId())/1024;
			default:
				return null;
			}
		} catch (Exception e) {
			logger.error("error get value : {}/{}",row,column,e);
			return null;
		}

	}


}
