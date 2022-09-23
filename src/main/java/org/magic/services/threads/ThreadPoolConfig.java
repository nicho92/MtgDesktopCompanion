package org.magic.services.threads;

public class ThreadPoolConfig {

	public enum THREADPOOL {SCHEDULE,FIXED,CACHED,SINGLE}

	private THREADPOOL threadPool;
	private int corePool;
	private boolean daemon;
	private String nameFormat;


	@Override
	public String toString() {
		return threadPool + " CorePool=" + corePool + " daemon="+daemon;
	}

	public String getNameFormat() {
		return nameFormat;
	}

	public void setNameFormat(String nameFormat) {
		this.nameFormat = nameFormat;
	}

	public THREADPOOL getThreadPool() {
		return threadPool;
	}
	public void setThreadPool(THREADPOOL threadPool) {
		this.threadPool = threadPool;
	}
	public int getCorePool() {
		return corePool;
	}
	public void setCorePool(int corePool) {
		this.corePool = corePool;
	}


	public boolean isDaemon() {
		return daemon;
	}


	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

}
