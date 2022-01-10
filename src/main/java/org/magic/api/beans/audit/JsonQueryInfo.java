package org.magic.api.beans.audit;

import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonQueryInfo extends AbstractAuditableItem {


	private static final long serialVersionUID = 1L;
	private String ip;
	private String contentType;
	private String method;
	private String url;
	private int status;
	private Map<String, String> params;
	private Set<String> attributes;
	private Map<String, String> headers;
	
	@Override
	public JsonObject toJson() {
		var jo = new JsonObject();
			jo.addProperty("url", getUrl());
			jo.addProperty("method", getMethod());
			jo.addProperty("start", getStart().toEpochMilli());
			jo.addProperty("end", getEnd().toEpochMilli());
			jo.addProperty("duration", getDuration());
			jo.addProperty("contentType", getContentType());
			
			var arr = new JsonArray();
			attributes.forEach(arr::add);
			jo.add("attributes", arr);
			
			var objHeaders = new JsonObject();
			headers.entrySet().forEach(e->objHeaders.addProperty(e.getKey(), e.getValue()));
			jo.add("headers", objHeaders);
			
			var objParams = new JsonObject();
			params.entrySet().forEach(e->objParams.addProperty(e.getKey(), e.getValue()));
			jo.add("params", objParams);
			
			
			
		return jo;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
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

	public void setHeaders(Map<String,String> headers) {
		this.headers = headers;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	
	
}
