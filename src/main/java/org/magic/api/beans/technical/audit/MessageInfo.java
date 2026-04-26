package org.magic.api.beans.technical.audit;

import com.google.gson.JsonObject;
import org.magic.api.beans.abstracts.AbstractAuditableItem;

public class MessageInfo extends AbstractAuditableItem {

	private static final long serialVersionUID = 1L;
	private JsonObject user;
	private JsonObject guild;
	private JsonObject channel;

	private String message;
	private String error;
	private String source;

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public JsonObject getUser() {
		return user;
	}

	public JsonObject getGuild() {
		return guild;
	}

	public JsonObject getChannel() {
		return channel;
	}

	public void setChannel(JsonObject channel) {
		this.channel = channel;
	}

	public void setGuild(JsonObject guild) {
		this.guild = guild;
	}

	public void setUser(JsonObject user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String contentRaw) {
		this.message = contentRaw;

	}

	public void setError(String message2) {
		this.error = message2;
	}

	public String getError() {
		return error;
	}

}
