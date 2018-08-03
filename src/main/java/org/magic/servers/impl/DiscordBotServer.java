package org.magic.servers.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.servers.impl.NavigableEmbed.EmbedButton;
import org.magic.services.MTGControler;
import org.magic.sorters.MagicPricesComparator;
import org.magic.tools.ColorParser;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBotServer extends AbstractMTGServer {

	private static final String THUMBNAIL_IMAGE = "THUMBNAIL_IMAGE";
	private static final String SHOWPRICE = "SHOWPRICE";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String TOKEN = "TOKEN";
	private static final String REGEX = "\\{(.*?)\\}";
	private JDA jda;
	private ListenerAdapter listener;
	private List<MagicCard> liste;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	private void initListener()
	{
		listener = new ListenerAdapter() {
			
			
			@Override
			public void onMessageReceived(MessageReceivedEvent event)
			{
				if (event.getAuthor().isBot()) 
					return;
				analyseCard(event);
			}
		};
	}
	
	private void analyseCard(MessageReceivedEvent event) {
		logger.debug("Received message :" + event.getMessage().getContentRaw() + " from " + event.getAuthor().getName()+ " in #" + event.getChannel().getName());
		
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(event.getMessage().getContentRaw());
		if(m.find())
		{
			String name=m.group(1).trim();
			MagicEdition ed = null;
			if(name.contains("|"))
			{
				ed = new MagicEdition();
				ed.setId(name.substring(name.indexOf('|')+1,name.length()).toUpperCase().trim());
				name=name.substring(0, name.indexOf('|')).trim();
			}
			MessageChannel channel = event.getChannel();
				channel.sendTyping().queue();
				try {
					liste = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(name, ed, false);
				}
				catch(Exception e)
				{
					liste=new ArrayList<>();
					logger.error(e);
				}
				
				if(liste.isEmpty())
				{
					channel.sendMessage("Sorry i can't found "+name ).queue();
					return;
				}
				
				NavigableEmbed.Builder builder = new NavigableEmbed.Builder(event.getChannel());
				for (int x = 0; x < liste.size(); x++) {
					MagicCard result = liste.get(x);
					BiFunction<MagicCard, Integer, MessageEmbed> getEmbed = (c, resultIndex) -> {
						MessageEmbed embed=parseCard(result);
						EmbedBuilder eb = new EmbedBuilder(embed);
						if (liste.size() > 1)
							eb.setFooter("Result " + (resultIndex + 1) + "/" + liste.size(), null);
						
						return eb.build();
					};
					int finalIndex = x;
					builder.addEmbed(() -> getEmbed.apply(result, finalIndex));
				}
				
				NavigableEmbed navEb = builder.build();
				
				
				if(liste.size()>1)
				{
					applyControl(EmbedButton.PREVIOUS.getIcon(), navEb.getMessage(), navEb.getWidth() > 1);
					applyControl(EmbedButton.NEXT.getIcon(), navEb.getMessage(), navEb.getWidth() > 1);
			
					ReactionListener rl = new ReactionListener(jda, navEb.getMessage(), false, 30 * 1000);
					rl.addController(event.getAuthor());
					rl.addResponse(EmbedButton.PREVIOUS.getIcon(), ev -> {
						navEb.setY(0);
						if (navEb.getX() > 0) navEb.left();
						applyControl(EmbedButton.PREVIOUS.getIcon(), navEb.getMessage(), navEb.getWidth() > 1);
					});
					rl.addResponse(EmbedButton.NEXT.getIcon(), ev -> {
						navEb.setY(0);
						if (navEb.getX() < navEb.getWidth() - 1) navEb.right();
						applyControl(EmbedButton.NEXT.getIcon(), navEb.getMessage(), navEb.getWidth() > 1);
					});

				}
			}	
			
		
		
	}

	
	private void applyControl(String emote, Message message, boolean enabled) {
			message.addReaction(emote).queue();
			if (!enabled) {
				message.getReactions().parallelStream().filter(r -> r.getReactionEmote().getEmote().getName().equals(emote))
								   .forEach(r -> {
									   	try {
											r.getUsers().submit().get().parallelStream().forEach(u -> r.removeReaction(u).queue());
										} catch (Exception e) {
											logger.error(e);
										}
								   	});
		}
	}
	
	
	private MessageEmbed parseCard(MagicCard mc) {
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription("");
		eb.setTitle(mc.getName()+ " " + mc.getCost());
		eb.setColor(ColorParser.getFullNameColorParse(mc.getColors()));
		StringBuilder temp = new StringBuilder();
		temp.append(mc.getTypes()+"\n");
		temp.append(mc.getText()).append("\n");
		temp.append("**Edition:** ").append(mc.getCurrentSet().getSet()).append("\n");
		temp.append("**Reserved:** ");
		if(mc.isReserved())
			temp.append(":white_check_mark: \n");
		else
			temp.append(":no_entry_sign:  \n");
		
		try {
			temp.append("**Collections:** "+MTGControler.getInstance().getEnabledDAO().listCollectionFromCards(mc).toString());
		} catch (SQLException e) {
			logger.error(e);
		}
		eb.setDescription(temp.toString());
	
		if(getString(THUMBNAIL_IMAGE).equalsIgnoreCase("THUMBNAIL"))
			eb.setThumbnail("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getCurrentSet().getMultiverseid()+"&type=card");
		else
			eb.setImage("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getCurrentSet().getMultiverseid()+"&type=card");
		
		if(getBoolean(SHOWPRICE))
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
		
		return eb.build();
	}
	
	@Override
	public void start() throws IOException {
		try {
			initListener();
			
			jda = new JDABuilder(AccountType.BOT)
							.setToken(getString(TOKEN))
							.addEventListener(listener)
							.build();
		
		} catch (LoginException e) {
			throw new IOException(e);
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
		return getBoolean(AUTOSTART);
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
	public Icon getIcon() {
		return new ImageIcon(DiscordBotServer.class.getResource("/icons/plugins/discord.png"));
	}
	
	@Override
	public void initDefault() 
	{
		setProperty(TOKEN,"");
		setProperty(AUTOSTART, "false");
		setProperty(SHOWPRICE, "true");
		setProperty(THUMBNAIL_IMAGE, "THUMBNAIL");
	}

}

//=================================================================EMBEDED MESSAGE
class NavigableEmbed extends ListenerAdapter {

	public enum EmbedButton {
		PREVIOUS("\u2b05"),
		NEXT("\u27a1");

		private String icon;

		EmbedButton(String icon) {
			this.icon = icon;
		}

		public String getIcon() {
			return icon;
		}
	}
	
	
	// Preferences
	private List<List<Supplier<MessageEmbed>>> embeds;
	private MessageChannel channel;

	// Internals
	private int xindex;
	private int yindex;
	private Message message;

	NavigableEmbed( List<List<Supplier<MessageEmbed>>> embeds,  MessageChannel channel) {
		this.embeds = new ArrayList<>();
		this.embeds.addAll(embeds);
		this.channel = channel;
		xindex = 0;
		yindex = 0;
		sendMessage();
	}

	public Message getMessage() {
		return message;
	}

	public int getX() {
		return xindex;
	}

	public int getY() {
		return yindex;
	}

	public int getWidth() {
		return embeds.size();
	}

	public int getHeight() {
		return embeds.parallelStream().mapToInt(List::size).max().orElse(0);
	}

	public int getHeightAt(int x) {
		if (x < 0 || x >= embeds.size()) throw new IllegalArgumentException("X is out of bounds.");
		return embeds.get(x).size();
	}

	public List<List<Supplier<MessageEmbed>>> getEmbeds() {
		return new ArrayList<>(embeds);
	}

	public void sendMessage() {
		MessageEmbed embed = embeds.get(xindex).get(yindex).get();
		try {
			if (message == null)
				message = channel.sendMessage(embed).submit().get();
			else {
				message = message.editMessage(embed).submit().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void setX(int x) {
		int newX = Math.min(Math.max(x, 0), getWidth() - 1);
		if (newX != xindex) {
			xindex = newX;
			sendMessage();
		}
	}

	public void setY(int y) {
		int newY = Math.min(Math.max(y, 0), embeds.get(xindex).size() - 1);
		if (newY != yindex) {
			yindex = newY;
			sendMessage();
		}
	}

	public void modX(int mod) {
		setX(getX() + mod);
	}

	public void modY(int mod) {
		setY(getY() + mod);
	}

	public void right() {
		modX(1);
	}

	public void left() {
		modX(-1);
	}

	public void up() {
		modY(-1);
	}

	public void down() {
		modY(1);
	}

	public static class Builder {
		private List<List<Supplier<MessageEmbed>>> embeds;
		private MessageChannel channel;

		public Builder( MessageChannel channel) {
			embeds = new ArrayList<>();
			this.channel = channel;
		}

		public Builder addEmbed( Supplier<MessageEmbed> embedSupplier) {
			
			ArrayList<Supplier<MessageEmbed>> list = new ArrayList<>();
			list.add(embedSupplier);
			embeds.add(list);
			return this;
		}

		public Builder addEmbed(Supplier<MessageEmbed> embedSupplier, int xIndex) {
			if (xIndex >= embeds.size())
				throw new IllegalArgumentException("xIndex is not within current bounds of the navigatable embed. " + xIndex + " >= " + embeds.size());
			List<Supplier<MessageEmbed>> xList = embeds.get(xIndex);
			xList.add(embedSupplier);
			return this;
		}

		public NavigableEmbed build() {
			return new NavigableEmbed(embeds, channel);
		}

	}
}

//=================================================================LISTENER
class ReactionListener extends ListenerAdapter {

	private static final long MAX_LIFE = 150000;

	private JDA jda;

	private Map<String, ReactionCallback> actionMap = new HashMap<>();
	private boolean oneTimeUse = false;
	private long expireTimeout = 0;
	private long startTime;
	private Timer expireTimer;
	private Message message;
	private Set<String> controllers = new HashSet<>();

	public ReactionListener(JDA jda, Message message, boolean oneTimeUse, long expireTimeout) {
		this.jda = jda;
		this.message = message;
		this.oneTimeUse = oneTimeUse;
		this.actionMap = new HashMap<>();
		this.expireTimeout = expireTimeout;
		this.expireTimer = new Timer();
		this.startTime = System.currentTimeMillis();
		enable();

		// Force disable after max life expiry
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				disable();
			}
		}, MAX_LIFE);
	}

	public void addResponse(String reaction, ReactionCallback cb) {
		actionMap.put(reaction, cb);
	}

	@Override
	public void onGenericMessageReaction(GenericMessageReactionEvent event) {
		
		if (message == null || event.getMessageIdLong() != message.getIdLong() || !controllers.contains(event.getUser().getId()))
			return;
		ReactionCallback cb = actionMap.getOrDefault(event.getReactionEmote().getName(), null);
	
		if (cb != null) {
			cb.exec(event);
			if (oneTimeUse) disable();
			else resetTimer();
		}
	}

	private void enable() {
		this.jda.addEventListener(this);
		if (this.expireTimeout > 0) resetTimer();
	}

	public void disable() {
		this.jda.removeEventListener(this);
		this.expireTimer.cancel();
	}

	private void resetTimer() {
		if (System.currentTimeMillis() - startTime >= MAX_LIFE) return;
		if (this.expireTimeout > 0) {
			this.expireTimer.cancel();
			this.expireTimer = new Timer();
			this.expireTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					disable();
				}
			}, expireTimeout);
		}
	}

	public void addController(User author) {
		controllers.add(author.getId());
	}

	public interface ReactionCallback {

		void exec(GenericMessageReactionEvent event);
	}
}




