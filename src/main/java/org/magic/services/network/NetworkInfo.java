package org.magic.services.network;

import java.io.Serializable;
import java.time.Instant;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class NetworkInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient HttpResponse response;
	private transient HttpRequestBase request;
	private long duration;
	private Instant start;
	private Instant end;

	
	public HttpResponse getResponse() {
		return response;
	}
	public void setReponse(HttpResponse response) {
		this.response = response;
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
	public void setRequest(HttpRequestBase req) {
		this.request=req;
	}
	
	public HttpRequestBase getRequest() {
		return request;
	}
	
	
	
	
	

}
