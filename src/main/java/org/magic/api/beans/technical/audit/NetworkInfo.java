package org.magic.api.beans.technical.audit;

import com.google.gson.JsonObject;
import java.net.URI;
import java.time.Instant;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.StatusLine;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.magic.api.beans.abstracts.AbstractAuditableItem;

public class NetworkInfo extends AbstractAuditableItem {

	private static final String REPONSES_MESSAGE = "reponsesMessage";
	private static final String CONTENT_TYPE = "contentType";
	private static final String SERVER_TYPE = "serverType";

	private static final long serialVersionUID = 1L;

	private transient ClassicHttpResponse response;
	private transient HttpUriRequestBase request;

	public ClassicHttpResponse getResponse() {
		return response;
	}
	public void setReponse(ClassicHttpResponse response) {
		this.response = response;
	}

	public void setRequest(HttpUriRequestBase req) {
		this.request = req;
	}

	public HttpUriRequestBase getRequest() {
		return request;
	}

	public String getServer() {
		return toJson().get(SERVER_TYPE).getAsString();
	}

	public String getContentType() {
		return toJson().get(CONTENT_TYPE).getAsString();
	}

	public void fromJson(JsonObject o) {

		try {

			request = new HttpUriRequestBase(o.get("method").getAsString(), o.get("url").getAsString()) {
				@Override
				public String getMethod() {
					return o.get("method").getAsString();
				}
			};
			request.setUri(URI.create(o.get("url").getAsString()));

		} catch (Exception _) {
			// do nothing
		}

		var sl = new StatusLine() {

			@Override
			public int getStatusCode() {
				return o.get("reponsesCode").getAsInt();
			}

			@Override
			public String getReasonPhrase() {

				if (o.get(REPONSES_MESSAGE) != null)
					return o.get(REPONSES_MESSAGE).getAsString();

				return "";
			}

			@Override
			public ProtocolVersion getProtocolVersion() {
				return new ProtocolVersion(o.get("protocol").getAsString(), 1, 1);
			}
		};

		response = new BasicClassicHttpResponse(sl.getStatusCode(), sl.getReasonPhrase());

		var entity = new BasicHttpEntity();
		entity.setContentType(o.get(CONTENT_TYPE).getAsString());

		response.setEntity(entity);
		response.setHeader("Server", o.get(SERVER_TYPE).getAsString());

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
		jo.addProperty("aborted", false);
		jo.addProperty("protocol", getRequest().getVersion() != null ? getRequest().getVersion().toString() : "HTTP/1.1");
		jo.addProperty("host", getRequest().getURI().getHost());

		if (getResponse() != null) {

			var servT = getResponse().getFirstHeader("Server");

			if (getResponse().getEntity() != null) {
				var contentT = getResponse().getEntity().getContentType();
				jo.addProperty(CONTENT_TYPE, contentT != null ? contentT.getValue() : "");
			} else {
				jo.addProperty(CONTENT_TYPE, "");
			}

			jo.addProperty(SERVER_TYPE, servT != null ? servT.getValue() : "");
			jo.addProperty(REPONSES_MESSAGE, getResponse().getReasonPhrase());
			jo.addProperty("reponsesCode", getResponse().getCode());
		}
		return jo;

	}

}
