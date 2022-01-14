package org.magic.api.beans.audit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import com.google.gson.JsonObject;

public class NetworkInfo extends AbstractAuditableItem{

	private static final long serialVersionUID = 1L;
	
	private transient HttpResponse response;
	private transient HttpRequestBase request;

	
	public HttpResponse getResponse() {
		return response;
	}
	public void setReponse(HttpResponse response) {
		this.response = response;
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
	
	
	@Override
	public void fromJson(JsonObject obj) {
		
		//can't do
	}
	
	
	@Override
	public JsonObject toJson() {
		var jo = new JsonObject();
		jo.addProperty("url", getRequest().getURI().toASCIIString());
		jo.addProperty("method", getRequest().getMethod());
		jo.addProperty("start", getStart().toEpochMilli());
		jo.addProperty("end", getEnd().toEpochMilli());
		jo.addProperty("duration", getDuration());
		jo.addProperty("aborted", getRequest().isAborted());
		jo.addProperty("protocol", getRequest().getRequestLine().getProtocolVersion().toString());
		jo.addProperty("host", getRequest().getURI().getHost());
		
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
