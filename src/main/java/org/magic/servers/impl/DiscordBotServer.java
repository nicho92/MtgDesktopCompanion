package org.magic.servers.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;
import org.magic.sorters.MagicPricesComparator;
import org.magic.tools.ColorParser;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBotServer extends AbstractMTGServer {

	private static final String SEARCHING = "OK i'm looking for it ! (let me few seconds...)";
	private static final String REGEX = "\\{(.*?)\\}";
	private JDA jda;
	private ListenerAdapter listener;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
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
				
					Pattern p = Pattern.compile(REGEX);
					Matcher m = p.matcher(event.getMessage().getContentRaw());
					while(m.find())
					{
						String name=m.group(1);
						MagicEdition ed = null;
						if(name.contains("|"))
						{
							ed = new MagicEdition();
							ed.setId(name.substring(name.indexOf('|')+1,name.length()).toUpperCase());
							name=name.substring(0, name.indexOf('|'));
						}
						try{
							if(getBoolean("SHOWPRICE"))
								channel.sendMessage(SEARCHING).queue();
							
							MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(name, ed, false).get(0);
							channel.sendMessage(parseCard(mc)).queue();
						}
						catch(Exception e)
						{
							logger.error(e);
							channel.sendMessage("Sorry i can't found "+name ).queue();
						}
					}
			}
		};
	}
	

	private MessageEmbed parseCard(MagicCard mc) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(mc.getName()+ " " + mc.getCost());
		eb.setColor(ColorParser.getColorParse(mc.getColors()));
		eb.getDescriptionBuilder().append(mc.getTypes().get(0)+"\n");
		eb.getDescriptionBuilder().append("Reserved :");
		if(mc.isReserved())
			eb.getDescriptionBuilder().append(":white_check_mark: \n");
		else
			eb.getDescriptionBuilder().append(":no_entry_sign:  \n");
		
		eb.getDescriptionBuilder().append(mc.getText()+"\n");
		eb.getDescriptionBuilder().append("\n**Editions**: ");
		mc.getEditions().forEach(me->eb.getDescriptionBuilder().append(me.getId()).append(","));
		try {
			eb.getDescriptionBuilder().append("\n**Collections :**"+MTGControler.getInstance().getEnabledDAO().listCollectionFromCards(mc).toString());
		} catch (SQLException e) {
			logger.error(e);
		}
	
		if(getString("THUMBNAIL_IMAGE").equalsIgnoreCase("THUMBNAIL"))
			eb.setThumbnail("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getCurrentSet().getMultiverseid()+"&type=card");
		else
			eb.setImage("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getCurrentSet().getMultiverseid()+"&type=card");
		
		
		if(getBoolean("SHOWPRICE"))
			MTGControler.getInstance().getEnabledPricers().forEach(prov->{
					try {
						List<MagicPrice> prices = prov.getPrice(null, mc);
						Collections.sort(prices, new MagicPricesComparator());
						eb.addField(prov.getName(),prices.get(0).getValue()+prices.get(0).getCurrency().getCurrencyCode(),true);
					} catch (Exception e) {
						logger.error(e);
					}
				}
			);
	
		logger.trace("sended json="+eb.build().toJSONObject());
		
		return eb.build();
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
		setProperty("SHOW_WAITING_MESSAGE", "false");
		setProperty("SHOWPRICE", "true");
		setProperty("THUMBNAIL_IMAGE", "THUMBNAIL");
	}
	
}
