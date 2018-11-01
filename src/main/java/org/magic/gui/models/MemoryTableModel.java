package org.magic.gui.models;

import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;

import org.magic.gui.abstracts.GenericTableModel;

public class MemoryTableModel extends GenericTableModel<MemoryMXBean> {

	public MemoryTableModel() {
		columns = new String[] {"heapInit","heapMax","heapCommit","heapUsed","nonHeapInit","nonHeapMax","nonHeapCommit","nonHeapUsed"};
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		MemoryMXBean t = items.get(row);
		try {
			switch (column) {
			case 0:return format(t.getHeapMemoryUsage().getInit());
			case 1:return format(t.getHeapMemoryUsage().getMax());
			case 2:return format(t.getHeapMemoryUsage().getCommitted());
			case 3:return format(t.getHeapMemoryUsage().getUsed());
			case 4:return format(t.getNonHeapMemoryUsage().getInit());
			case 5:return format(t.getNonHeapMemoryUsage().getMax());
			case 6:return format(t.getNonHeapMemoryUsage().getCommitted());
			case 7:return format(t.getNonHeapMemoryUsage().getUsed());
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}

	private double format(long init) {
		return (init / (1024 * 1024));
	}
	
	
}
