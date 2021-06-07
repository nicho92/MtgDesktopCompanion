package org.magic.api.notifiers.impl;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class DiscordNotifier extends AbstractMTGNotifier {

	public static final int MAXSIZE=2000;
	
	
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
			
			jda = JDABuilder.createDefault(getString("TOKEN")).build().awaitReady();
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
			
			if(message.length()>MAXSIZE)
			{
				logger.warn("Message is too long : " + msg.length() + ">"+MAXSIZE+". Will truncate it");
				message=message.substring(0, MAXSIZE);
			}
			
			logger.debug("send " + message +": File="+notification.getFile());
	
			if(notification.getFile()==null)
			{
				chan.sendMessage(message).queue();
			}
			else
			{
				MessageAction ret=chan.sendFile(notification.getFile(),msg.toString());
				chan.sendMessage(ret.complete()).queue();
			}
			
			
			
			
		} catch (LoginException e) {
			logger.error("couldn't init login",e);
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
	public void initDefault() {
		setProperty("TOKEN","");
		setProperty("CHANNELID", "");
	}
	
	@Override
	public String getVersion() {
		return JDAInfo.VERSION;
	}

}
