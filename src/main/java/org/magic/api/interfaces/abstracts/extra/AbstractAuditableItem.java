package org.magic.api.interfaces.abstracts.extra;

import java.io.Serializable;
import java.time.Instant;

public abstract class AbstractAuditableItem implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Instant start;
	protected Instant end;
	protected long duration;

	@Override
	public String toString() {
		return getClass()+""+start+""+end+""+duration;
	}




	protected AbstractAuditableItem() {
		start= Instant.now();
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

}
