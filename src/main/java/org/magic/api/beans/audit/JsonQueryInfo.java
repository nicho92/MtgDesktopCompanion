package org.magic.api.beans.audit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgent.ImmutableUserAgent;

public class JsonQueryInfo extends AbstractAuditableItem {


	private static final long serialVersionUID = 1L;
	private String ip;
	private String contentType;
	private String method;
	private String url;
	private int status;
	private Map<String, String> params;
	private Map<String, Object> attributes;
	private Map<String, String> headers;
	private UserAgent userAgent;
	private String sessionId;
	private String path;
	private Map<String, String> queryParams;
	
	public JsonQueryInfo() {
		params=new HashMap<>();
		attributes = new HashMap<>();
		headers = new HashMap<>();
	}
	
	
	@Override
	public JsonObject toJson() {
		var jo = new JsonObject();
			jo.addProperty("url", getUrl());
			jo.addProperty("method", getMethod());
			jo.addProperty("start", getStart().toEpochMilli());
			jo.addProperty("end", getEnd().toEpochMilli());
			jo.addProperty("duration", getDuration());
			jo.addProperty("contentType", getContentType());
			jo.addProperty("ip", getIp());
			jo.addProperty("sessionID", getSessionId());
			jo.addProperty("path", getPath());
			
			
			var objQParams= new JsonObject();
			queryParams.entrySet().forEach(e->objQParams.addProperty(e.getKey(), String.valueOf(e.getValue())));
			jo.add("query", objQParams);
			
			
			var objAttributes = new JsonObject();
			attributes.entrySet().forEach(e->objAttributes.addProperty(e.getKey(), String.valueOf(e.getValue())));
			jo.add("attributes", objAttributes);
			
			var objHeaders = new JsonObject();
			headers.entrySet().forEach(e->objHeaders.addProperty(e.getKey(), e.getValue()));
			jo.add("headers", objHeaders);
			
			var objParams = new JsonObject();
			params.entrySet().forEach(e->objParams.addProperty(e.getKey(), e.getValue()));
			jo.add("params", objParams);
			
			var objUa = new JsonObject();
			if(userAgent!=null)
				userAgent.toMap().entrySet().forEach(e->objUa.addProperty(e.getKey(), e.getValue()));

			jo.add("userAgent", objUa);
			
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
	
	public void setAttributs(Map<String,Object> attributes) {
		this.attributes=attributes;
	}
	
	public Map<String,Object> getAttributes() {
		return attributes;
	}

	public void setHeaders(Map<String,String> headers) {
		this.headers = headers;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setUserAgent(ImmutableUserAgent ua) {
		this.userAgent=ua;
		
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String id) {
		this.sessionId=id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String servletPath) {
		this.path=servletPath;
	}
	
	public Map<String, String> getQueryParams() {
		return queryParams;
	}


	public void setQuery(Map<String, String> map) {
		this.queryParams=map;
		
	}

	
	
}
