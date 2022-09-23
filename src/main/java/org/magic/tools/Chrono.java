package org.magic.tools;

public class Chrono {

	private long value;

	public Chrono()
	{
		//do nothing
	}

	public void start()
	{
		value=System.currentTimeMillis();
	}


	public long stop()
	{
		return stopInMillisecond()/1000;
	}

	public long stopInMillisecond()
	{
		return System.currentTimeMillis()-value;
	}

}
