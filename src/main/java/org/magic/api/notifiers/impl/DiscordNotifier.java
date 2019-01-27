package org.magic.api.notifiers.impl;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.TextChannel;

public class DiscordNotifier extends AbstractMTGNotifier {

	public static final int MAXSIZE=2000;
	
	@Override
	public void send(MTGNotification notification) throws IOException {
		 
		JDA jda=null;
		try {
			
			jda = new JDABuilder(AccountType.BOT).setToken(getString("TOKEN")).build().awaitReady();
			TextChannel chan = jda.getTextChannelById(getLong("CHANNELID"));
			notification.setSender(String.valueOf(jda.getSelfUser()));
			StringBuilder msg = new StringBuilder();
			
			String emoji="";
			switch(notification.getType())
			{
				case ERROR : emoji=":error:";break;
				case WARNING: emoji=":warning:";break;
				case INFO : emoji=":information_source:";break;
				default : emoji="";
			}
			
			msg.append(emoji).append(notification.getMessage());
			msg.append("*").append(notification.getTitle()).append("*\n");
			
			String message=msg.toString();
			
			if(message.length()>MAXSIZE)
			{
				logger.error("Message is too long : " + msg.length() + ">"+MAXSIZE+". Will truncate it");
				message=message.substring(0, MAXSIZE);
			}
			
			logger.debug("send " + message);
	
			if(notification.getFile()==null)
				chan.sendMessage(message).queue();
			else
				chan.sendFile(notification.getFile(),msg.toString());
			
			
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
