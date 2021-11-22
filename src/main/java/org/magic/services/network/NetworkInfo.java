package org.magic.services.network;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.time.Instant;

public class NetworkInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HttpURLConnection connection;
	private long duration;
	private Instant start;
	private Instant end;
	
	
	public NetworkInfo(HttpURLConnection con) {
		this.connection=con;
	}
	
	
	public HttpURLConnection getConnection() {
		return connection;
	}
	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
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
	}
	
	
	
	
	
	

}
