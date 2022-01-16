package org.magic.api.beans.audit;

import java.util.HashMap;
import java.util.Map;

import nl.basjes.parse.useragent.UserAgent;

public class JsonQueryInfo extends AbstractAuditableItem {


	private static final long serialVersionUID = 1L;
	private String ip;
	private String contentType;
	private String method;
	private String url;
	private int status;
	private Map<String, String> params;
	private transient Map<String, Object> attributes;
	private Map<String, String> headers;
	private UserAgent userAgent;
	private String sessionId;
	private String path;
	private Map<String, String> queryParams;
	
	
	public JsonQueryInfo() {
		super();
		params=new HashMap<>();
		attributes = new HashMap<>();
		headers = new HashMap<>();
		queryParams = new HashMap<>();
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

	public void setUserAgent(UserAgent ua) {
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

	
	public UserAgent getUserAgent() {
		return userAgent;
	}
	
	
	
}
