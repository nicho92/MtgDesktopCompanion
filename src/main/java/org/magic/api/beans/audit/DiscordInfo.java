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
	private String error;

	@Override
	public JsonObject toJson() {
		var obj = new JsonObject();
			obj.addProperty("start", getStart().toEpochMilli());
			obj.addProperty("end", getEnd().toEpochMilli());
			obj.addProperty("duration", getDuration());
			
		var userObj = new JsonObject();
			userObj.addProperty("id", user.getId());
			userObj.addProperty("name", user.getName());
			userObj.addProperty("descriminator", user.getDiscriminator());
			userObj.addProperty("mention", user.getAsMention());
			userObj.addProperty("avatar", user.getAvatarUrl());
			userObj.addProperty("id", user.getId());
		obj.add("user", userObj);
		
		if(guild!=null)
		{
			var guildObj = new JsonObject();
				guildObj.addProperty("id", guild.getId());
				guildObj.addProperty("banner", guild.getBannerUrl());
				guildObj.addProperty("icon", guild.getIconUrl());
				guildObj.addProperty("name", guild.getName());
				guildObj.addProperty("description", guild.getDescription());
			obj.add("guild", guildObj);
		}
		
		if(channel!=null)
		{
			var channelObj = new JsonObject();
				channelObj.addProperty("name", channel.getName());
				channelObj.addProperty("id", channel.getId());
				channelObj.addProperty("mention", channel.getAsMention());
				obj.add("channel", channelObj);
		}
			
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

	public void setError(String message2) {
		this.error=message2;
	}
	
	public String getError() {
		return error;
	}

}
