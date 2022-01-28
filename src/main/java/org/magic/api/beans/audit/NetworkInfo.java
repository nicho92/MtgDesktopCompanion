package org.magic.api.beans.audit;

import java.net.URI;
import java.time.Instant;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;

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
	
	public void fromJson(JsonObject o) {
		
		try {
			
			request = new HttpRequestBase() {
				@Override
				public String getMethod() {
					return o.get("method").getAsString();
				}
			};
			request.setURI(URI.create(o.get("url").getAsString()));
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		StatusLine sl = new StatusLine() {
			
			@Override
			public int getStatusCode() {
				return o.get("reponsesCode").getAsInt();
			}
			
			@Override
			public String getReasonPhrase() {
				
				if(o.get("reponsesMessage")!=null)
					return o.get("reponsesMessage").getAsString();
				
				return "";
			}
			
			@Override
			public ProtocolVersion getProtocolVersion() {
				return new ProtocolVersion(o.get("protocol").getAsString(),1,1);
			}
		};
		
		
		 response = new BasicHttpResponse(sl);
		 
		 BasicHttpEntity entity = new BasicHttpEntity();
		 				 entity.setContentType(o.get("contentType").getAsString());
		 
		 response.setEntity(entity);
		 response.setHeader("Server", o.get("serverType").getAsString());
		 
		 
		 start = Instant.ofEpochMilli(o.get("start").getAsLong());
		 end = Instant.ofEpochMilli(o.get("end").getAsLong());
		 duration = o.get("duration").getAsLong();
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
