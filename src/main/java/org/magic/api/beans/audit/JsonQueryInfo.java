package org.magic.api.beans.audit;

import java.util.Map;
import java.util.Set;

import org.magic.api.exports.impl.JsonExport;
import org.magic.services.MTGConstants;

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
		return new JsonExport().toJsonElement(this).getAsJsonObject();

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
