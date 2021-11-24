package org.magic.services.network;

import java.io.Serializable;
import java.time.Instant;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import com.google.gson.JsonObject;

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
	
	public String getServer()
	{
		return toJson().get("serverType").getAsString();
	}

	public String getContentType()
	{
		return toJson().get("contentType").getAsString();
	}
	
	
	public JsonObject toJson() {
		var jo = new JsonObject();
		jo.addProperty("url", getRequest().getURI().toASCIIString());
		jo.addProperty("method", getRequest().getMethod());
		jo.addProperty("start", getStart().toEpochMilli());
		jo.addProperty("end", getEnd().toEpochMilli());
		jo.addProperty("duration", getDuration());
		jo.addProperty("aborted", getRequest().isAborted());
		jo.addProperty("protocol", getRequest().getRequestLine().getProtocolVersion().toString());
		
		if(getResponse()!=null) {
			
			var servT =getResponse().getFirstHeader("Server");
			
			if(getResponse().getEntity()!=null)
			{
				var contentT = getResponse().getEntity().getContentType();
				jo.addProperty("contentType", contentT!=null?contentT.getValue():"");
			}
			else
			{
				jo.addProperty("contentType", "");	
			}
			
			jo.addProperty("serverType", servT!=null?servT.getValue():"");
			jo.addProperty("reponsesMessage", getResponse().getStatusLine().getReasonPhrase());
			jo.addProperty("reponsesCode", getResponse().getStatusLine().getStatusCode());
		}
		return jo;
	
	}
	
	
	
	
	

}
