package org.magic.api.beans.audit;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public class JsonQueryInfo implements Serializable {


	private static final long serialVersionUID = 1L;
	private long duration;
	private Instant start;
	private Instant end;
	private String ip;
	private String contentType;
	private String method;
	private String url;
	private int status;
	private Map<String, String> params;
	private Set<String> attributes;
	private Set<String> headers;
	
	@Override
	public String toString() {
		return getStart() + " " + getDuration() + " " + getUrl();
	}
	
	public JsonQueryInfo() {
		start=Instant.now();
	}

	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
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
		setDuration(getEnd().toEpochMilli()-getStart().toEpochMilli());
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setStatus(int status) {
		this.status=status;
	}
	
	public int getStatus() {
		return status;
	}

	public void setParameters(Map<String, String> params) {
		this.params=params;
		
	}

	public Map<String, String> getParams() {
		return params;
	}
	
	public void setAttributs(Set<String> attributes) {
		this.attributes=attributes;
	}
	
	public Set<String> getAttributes() {
		return attributes;
	}

	public void setHeaders(Set<String> headers) {
		this.headers = headers;
	}
	
	public Set<String> getHeaders() {
		return headers;
	}
	
}
