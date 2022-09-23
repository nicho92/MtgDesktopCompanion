package org.magic.api.notifiers.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.services.MTGConstants;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.utils.FileUpload;


public class DiscordNotifier extends AbstractMTGNotifier {


	@Override
	public boolean isExternal() {
		return true;
	}

	public void sendIssues(MTGNotification not)
	{
		sendMessage(not,576698603746230273L);

	}

	@Override
	public void send(MTGNotification notification) throws IOException {
		sendMessage(notification,getLong("CHANNELID"));
	}



	public void sendMessage(MTGNotification notification,long chanID) {

		JDA jda=null;
		try {

			jda = JDABuilder.createDefault(getAuthenticator().get("TOKEN")).build().awaitReady();
			var chan = jda.getTextChannelById(chanID);
			notification.setSender(String.valueOf(jda.getSelfUser()));
			var msg = new StringBuilder();

			var emoji="";
			switch(notification.getType())
			{
				case ERROR : emoji=":error:";break;
				case WARNING: emoji=":warning:";break;
				case INFO : emoji=":information_source:";break;
				default : emoji="";
			}

			msg.append(emoji).append(notification.getMessage());
			msg.append("*").append(notification.getTitle()).append("*\n");

			var message=msg.toString();

			if(message.length()>MTGConstants.DISCORD_MAX_CHARACTER)
			{
				logger.warn("Message is too long : {} > {}. Will truncate it",msg.length(),MTGConstants.DISCORD_MAX_CHARACTER);
				message=message.substring(0, MTGConstants.DISCORD_MAX_CHARACTER);
			}

			logger.debug("send {} File : {} ",message,notification.getFile());

			if(notification.getFile()==null)
			{
				chan.sendMessage(message).queue();
			}
			else
			{
				var ret=chan.sendFiles(FileUpload.fromData(notification.getFile()));
				chan.sendMessage(ret.complete().getContentDisplay()).queue();
			}




		} catch (InterruptedException e) {
			logger.error("error await",e);
			 Thread.currentThread().interrupt();

		}
		finally {
			if(jda!=null)
				jda.shutdown();
		}

	}

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.MARKDOWN;
	}

	@Override
	public String getName() {
		return "Discord";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("CHANNELID", "");
	}

	@Override
	public String getVersion() {
		return JDAInfo.VERSION;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("TOKEN");
	}


}
