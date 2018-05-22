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
import net.dv8tion.jda.core.entities.TextChannel;

public class DiscordNotifier extends AbstractMTGNotifier {

	
	public static void main(String[] args) throws IOException {
		new DiscordNotifier().send(new MTGNotification("Test", "<html>Test de :mana0: via apps</html>", MESSAGE_TYPE.INFO));
	}
	
	@Override
	public void send(MTGNotification notification) throws IOException {
		 
		JDA jda=null;
		try {
			//TODO emoji
			jda = new JDABuilder(AccountType.BOT).setToken(getString("TOKEN")).buildBlocking();
			TextChannel chan = jda.getTextChannelById(getLong("CHANNELID"));
			notification.setSender(jda.getSelfUser().getName());
			
			MessageBuilder msg = new MessageBuilder();
			msg.append(notification.getMessage());
			
			if(notification.getFile()==null)
				chan.sendMessage(msg.build()).queue();
			else
				chan.sendFile(notification.getFile(),msg.build());
		} catch (LoginException e) {
			logger.error("couldn't init login",e);
		} catch (InterruptedException e) {
			logger.error("Interupted !",e);
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

}
