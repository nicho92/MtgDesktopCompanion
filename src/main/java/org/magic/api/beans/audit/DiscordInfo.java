package org.magic.api.beans.audit;

import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordInfo extends AbstractAuditableItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	private Guild guild;
	private MessageChannel channel;
	private String message;

	@Override
	public JsonObject toJson() {
		var obj = new JsonObject();
		
		//TODO
		
		return obj;
	}

	public User getUser() {
		return user;
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public MessageChannel getChannel() {
		return channel;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	
	public void setAuthor(User author) {
		this.user=author;
		
	}

	public void setGuild(Guild guild) {
		this.guild=guild;
		
	}

	public void setChannel(MessageChannel channel) {
		this.channel=channel;
		
	}

	public void setMessage(String contentRaw) {
		this.message=contentRaw;
		
	}

}
