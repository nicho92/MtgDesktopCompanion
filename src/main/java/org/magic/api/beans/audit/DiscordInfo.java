package org.magic.api.beans.audit;

import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordInfo extends AbstractAuditableItem {

	private static final long serialVersionUID = 1L;
	private JsonObject user;
	private JsonObject guild;
	private JsonObject channel;
	private String message;
	private String error;

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
	
	public void parse(User author) {
		user = new JsonObject();
		user.addProperty("id", author.getId());
		user.addProperty("name", author.getName());
		user.addProperty("descriminator", author.getDiscriminator());
		user.addProperty("mention", author.getAsMention());
		user.addProperty("avatar", author.getAvatarUrl());
		user.addProperty("id", author.getId());
		
	}

	public void parse(Guild g) {
		guild = new JsonObject();
		guild.addProperty("id", g.getId());
		guild.addProperty("banner", g.getBannerUrl());
		guild.addProperty("icon", g.getIconUrl());
		guild.addProperty("name", g.getName());
		guild.addProperty("description", g.getDescription());
		guild.addProperty("etest", g.getVanityUrl());
		
	}

	public void parse(MessageChannel c) {
		channel = new JsonObject();
		channel.addProperty("name", c.getName());
		channel.addProperty("id", c.getId());
		channel.addProperty("mention", c.getAsMention());
	}

	public void setMessage(String contentRaw) {
		this.message=contentRaw;
		
	}

	public void setError(String message2) {
		this.error=message2;
	}
	
	public String getError() {
		return error;
	}

}
