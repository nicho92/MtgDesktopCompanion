package org.magic.servers.impl;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.console.Command;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBotServer extends AbstractMTGServer {

	private JDA jda;
	private ListenerAdapter listener;
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		new DiscordBotServer().start();
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	private void initListener()
	{
		listener = new ListenerAdapter() {
			@Override
			public void onMessageReceived(MessageReceivedEvent event) {
				
				if (event.getAuthor().isBot()) 
					return;
				
				logger.debug("Received message :" + event.getMessage().getContentRaw() + " from " + event.getAuthor().getName()+ " in #" + event.getChannel().getName());
				
				MessageChannel channel = event.getChannel();
				
				try {
					String[] msg = event.getMessage().getContentRaw().split(" ");
					Command com = MTGConsoleHandler.commandFactory(msg[0]);
					String ret = com.run(msg).toString();
					if(ret.length()>2000)
						ret=ret.substring(0, 2000);
			
					channel.sendMessage(ret).queue();
					
				} catch (Exception e) {
					logger.error(e);
					channel.sendMessage(e.getMessage()).queue();
				} 
				
			}
		};
	}
	
	@Override
	public void start() throws IOException {
		try {
			initListener();
			
			jda = new JDABuilder(AccountType.BOT)
							.setToken(getString("TOKEN"))
							.addEventListener(listener)
							.buildBlocking();
		
		} catch (LoginException e) {
			throw new IOException(e);
		} catch (InterruptedException e) {
			logger.error("Interrupted",e);
			Thread.currentThread().interrupt();
		}
		
	}

	@Override
	public void stop() throws IOException {
		if(jda!=null)
		{
			jda.shutdown();
			jda.getPresence().setPresence(OnlineStatus.OFFLINE,false);
		}
	}

	@Override
	public boolean isAlive() {
		if(jda!=null)
			return jda.getStatus().equals(JDA.Status.CONNECTED);
		return false;
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public String description() {
		return "Query your MTGDesktopCompanion via discord Channel";
	}

	@Override
	public String getName() {
		return "Discord Bot";
	}

	@Override
	public void initDefault() 
	{
		setProperty("TOKEN","");
		setProperty("AUTOSTART", "false");
	}
	
}
