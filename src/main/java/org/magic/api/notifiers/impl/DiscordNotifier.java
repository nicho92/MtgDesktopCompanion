package org.magic.api.notifiers.impl;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class DiscordNotifier extends AbstractMTGNotifier {

	
	public static void main(String[] args) throws IOException {
		new DiscordNotifier().send(new MTGNotification("Test", "Test de notification via apps", MESSAGE_TYPE.INFO));
	}
	
	@Override
	public void send(MTGNotification notification) throws IOException {
		 
		
		try {
			
			JDA jda = new JDABuilder(AccountType.BOT).setToken(getString("TOKEN")).buildBlocking();
			TextChannel chan = jda.getTextChannelById(getLong("CHANNELID"));
			notification.setSender(jda.getSelfUser().getName());
			
			MessageBuilder msg = new MessageBuilder();
			msg.append(notification.getMessage());
			
			
			chan.sendMessage(msg.build()).queue();
			
			jda.shutdown();
			
		} catch (LoginException e) {
			logger.error("couldn't init login",e);
		} catch (InterruptedException e) {
			logger.error("Interupted !",e);
			Thread.currentThread().interrupt();
		}
		 		 
	}

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.TEXT;
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

}
