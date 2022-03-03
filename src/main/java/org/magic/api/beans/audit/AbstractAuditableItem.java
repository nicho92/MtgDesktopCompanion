package org.magic.api.beans.audit;

import java.io.Serializable;
import java.time.Instant;

import org.apache.commons.lang3.ArrayUtils;

public abstract class AbstractAuditableItem implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Instant start;
	protected Instant end;
	protected long duration;
	protected StackTraceElement[] stackTrace;
	
	
	
	
	
	protected AbstractAuditableItem() {
		start= Instant.now();
		
		
		if(Thread.currentThread().getStackTrace().length>13)
			stackTrace = ArrayUtils.subarray(Thread.currentThread().getStackTrace(),3,14);
		else
			stackTrace = Thread.currentThread().getStackTrace();
		
		
	}

	public Instant getStart() {
		return start;
	}

	public void setStart(Instant start) {
		this.start = start;
	}

	public Instant getEnd() {
		return end;
	}

	public void setEnd(Instant end) {
		this.end = end;
		setDuration(getEnd().toEpochMilli()-getStart().toEpochMilli());
	}

	public long getDuration() {
		return duration;
	}


	public void setDuration(long duration) {
			this.duration=duration;
	}
	
	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	
	
	
	
	
	
}
