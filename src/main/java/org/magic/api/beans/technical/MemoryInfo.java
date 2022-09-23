package org.magic.api.beans.technical;

import java.io.Serializable;

import org.magic.tools.UITools;

public class MemoryInfo implements Serializable, Comparable<MemoryInfo> {

	private static final long serialVersionUID = 1L;

	private Class<?> classe;
	private long count;
	private long size;

	public MemoryInfo() {
	}

	public MemoryInfo(Class<?> classe, long count, long size) {
		this.classe = classe;
		this.count = count;
		this.size = size;
	}


	public double getAvg() {
		try {
			return (double)size/count;
		}
		catch(Exception e)
		{
			return 0;
		}
	}


	public Class<?> getClasse() {
		return classe;
	}
	public void setClasse(Class<?> classe) {
		this.classe = classe;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}


	@Override
	public String toString() {
		return "COUNT="+getCount()+"\tSIZE="+getSize()+"\tAVG="+UITools.formatDouble(getAvg()) +"\t"+getClasse();
	}

	@Override
	public int compareTo(MemoryInfo mem) {
		return (int) (getCount()-mem.getCount());
	}


}
