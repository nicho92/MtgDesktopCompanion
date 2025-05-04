package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
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
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.api.mkm.modele.InsightElement;
import org.api.mkm.services.InsightService;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat.FORMATS;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.audit.DiscordInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.api.sorters.MagicPricesComparator;
import org.magic.api.sorters.PricesCardsShakeSorter;
import org.magic.api.sorters.PricesCardsShakeSorter.SORT;
import org.magic.servers.impl.NavigableEmbed.EmbedButton;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import nl.basjes.parse.useragent.yauaa.shaded.org.apache.commons.lang3.ArrayUtils;

public class DiscordBotServer extends AbstractMTGServer {


	private static final String THUMBNAIL = "THUMBNAIL";
	private static final String EXTERNAL_LINK = "EXTERNAL_LINK";
	private static final String ACTIVITY = "ACTIVITY";
	private static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
	private static final String THUMBNAIL_IMAGE = "THUMBNAIL_IMAGE";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String TOKEN = "TOKEN";
	private static final String SHOWCOLLECTIONS = "SHOW_COLLECTIONS";
	private static final String PRICE_KEYWORDS = "PRICE_KEYWORDS";
	private static final String RESULTS_SHAKES="RESULTS_SHAKES";
	private static final String REGEX ="\\{(.*?)\\}";
	private JDA jda;
	private ListenerAdapter listener;


	@Override
	public String getVersion() {
		return JDAInfo.VERSION;
	}


	private void initListener()
	{
		listener = new ListenerAdapter() {
			@Override
			public void onMessageReceived(MessageReceivedEvent event)
			{
				if (event.getAuthor().isBot())
					return;
				
				if(ArrayUtils.contains(getArray("BLOCKED_USERS"),event.getAuthor().getName())){
					logger.warn("{} is blocked because {}", event.getAuthor().getName(), event.getAuthor().isBot()?"is Bot":"is blocked user");
					return;
				}

				analyseMessage(event);
			}

			@Override
			public void onReady(ReadyEvent event) {
				logger.info("Server {} started",getName());
			}
		};
	}


	public JsonObject toJsonDetails()
	{
		var jo  = new JsonObject();
		if(isAlive()) {

		var arrGuilds = new JsonArray();
			jda.getGuilds().forEach(g->arrGuilds.add(DiscordInfo.parse(g)));
			jo.add("guilds", arrGuilds);
			jo.add("user", DiscordInfo.parse(jda.getSelfUser()));
			try {
				jo.addProperty("presenceActivity", String.valueOf(jda.getPresence().getActivity()));
				jo.addProperty("presenceValue", jda.getPresence().getActivity().getName());
			}catch(Exception e)
			{
				logger.error(e);
			}

		}

		return jo;
	}



	private void analyseMessage(MessageReceivedEvent event) {
		var info = new DiscordInfo();
		info.setUser(DiscordInfo.parse(event.getAuthor()));
		info.setChannel(DiscordInfo.parse(event.getChannel()));


		info.setMessage(event.getMessage().getContentRaw());

		var p = Pattern.compile(REGEX);
		var m = p.matcher(event.getMessage().getContentRaw());
		if(m.find())
		{

			if(event.isFromGuild())
			{
				info.setGuild(DiscordInfo.parse(event.getGuild()));
				logger.info("Received channel message : {} from {} in {}#{}",event.getMessage().getContentRaw(),event.getAuthor().getName(),event.getGuild().getName(),event.getChannel().getName());
			}
			else
				logger.info("Received MP message : {} from {}",event.getMessage().getContentRaw(),event.getAuthor().getName());

			var name=m.group(1).trim();

			logger.debug("parsing {} values",name);

			if(name.equalsIgnoreCase("help"))
			{
				responseHelp(event);
				info.setEnd(Instant.now());
				AbstractTechnicalServiceManager.inst().store(info);

				return;
			}

			if(name.toLowerCase().startsWith("set|"))
			{
				try {
					boolean noFoil= StringUtils.containsAnyIgnoreCase(info.getMessage(),"nofoil","no foil");
					boolean foilOnly = StringUtils.containsAnyIgnoreCase(info.getMessage(),"foil","onlyfoil");

					responseChardShake(event,name,noFoil,foilOnly);
				} catch (IOException e) {
					info.setError(e.getMessage());
					event.getChannel().sendMessage("Hoopsy...error for "+e.getMessage()).queue();
				}
				info.setEnd(Instant.now());
				AbstractTechnicalServiceManager.inst().store(info);

				return;
			}

			if(name.toLowerCase().startsWith("format|"))
			{
				try {
					responseFormats(event,name);
				} catch (IOException e) {
					info.setError(e.getMessage());
					event.getChannel().sendMessage(e.getMessage()).queue();
				}
				info.setEnd(Instant.now());
				AbstractTechnicalServiceManager.inst().store(info);

				return;
			}


			if(name.toLowerCase().startsWith("mkm"))
			{
				try {
					responseMkmStock(event);
				} catch (IOException e) {
					info.setError(e.getMessage());
					event.getChannel().sendMessage(e.getMessage()).queue();
				}
				info.setEnd(Instant.now());
				AbstractTechnicalServiceManager.inst().store(info);

				return;
			}

			responseSearch(event,name,info);
			info.setEnd(Instant.now());
			AbstractTechnicalServiceManager.inst().store(info);
		}
	}


	private void responseFormats(MessageReceivedEvent event,String content) throws IOException {
		String format="";
		try {
			event.getChannel().sendTyping().queue();
			format=content.substring(content.indexOf('|')+1,content.length()).toUpperCase().trim();
			List<CardShake> ret= MTG.getEnabledPlugin(MTGDashBoard.class).getShakerFor(FORMATS.valueOf(format));
			Collections.sort(ret, new PricesCardsShakeSorter(SORT.DAY_PERCENT_CHANGE,false));

			var res = StringUtils.substring(notifFormater.generate(FORMAT_NOTIFICATION.MARKDOWN, ret.subList(0, getInt(RESULTS_SHAKES)),CardShake.class),0,MTGConstants.DISCORD_MAX_CHARACTER);

			event.getChannel().sendMessage(res).queue();

		}
		catch(IllegalArgumentException e)
		{
			logger.error(e);
			throw new IOException("format " + format + " is not found... try with : " + StringUtils.join(FORMATS.values(),","));
		}
		catch(Exception e)
		{
			logger.error(e);
			throw new IOException("Hoopsy Error ");
		}

	}





	private void responseMkmStock(MessageReceivedEvent event) throws IOException {
		event.getChannel().sendTyping().queue();
		var serv = new InsightService();

			Collections.sort(serv.getHighestPercentStockReduction(), (InsightElement o1, InsightElement o2) -> {
					if(o1.getChangeValue()>o2.getChangeValue())
						return -1;
					else
						return 1;
			});

			var res =  StringUtils.substring(notifFormater.generate(FORMAT_NOTIFICATION.MARKDOWN, serv.getHighestPercentStockReduction(),InsightElement.class),0,MTGConstants.DISCORD_MAX_CHARACTER);
			event.getChannel().sendMessage(StringUtils.substring(res,0,MTGConstants.DISCORD_MAX_CHARACTER)).queue();

	}


	private void responseChardShake(MessageReceivedEvent event,String name, boolean noFoil, boolean foilOnly) throws IOException {

			event.getChannel().sendTyping().queue();


				logger.debug("search {} with nofoil={} and foilOnly={}",name,noFoil,foilOnly);

				String ed=name.substring(name.indexOf('|')+1,name.length()).toUpperCase().trim();
				var  eds = MTG.getEnabledPlugin(MTGDashBoard.class).getShakesForEdition(new MTGEdition(ed));
				var chks = eds.getShakes().stream().filter(cs->cs.getPriceDayChange()!=0).sorted(new PricesCardsShakeSorter(SORT.DAY_PERCENT_CHANGE,false)).toList();

				if(noFoil)
					chks = chks.stream().filter(cs->!cs.isFoil()).toList();
				else if(foilOnly)
					chks = chks.stream().filter(CardShake::isFoil).toList();

				var res =  StringUtils.substring(notifFormater.generate(FORMAT_NOTIFICATION.MARKDOWN, chks.subList(0, getInt(RESULTS_SHAKES)),CardShake.class),0,MTGConstants.DISCORD_MAX_CHARACTER);
				event.getChannel().sendMessage(res).queue();

	}


	private void responseHelp(MessageReceivedEvent event) {
		var channel = event.getChannel();
		channel.sendTyping().queue();
		channel.sendMessage(":face_with_monocle: It's simple "+event.getAuthor().getName()+", put card name in bracket like {Black Lotus} or {Black Lotus| LEA} if you want to specify a set\n "
				+ "If you want to have prices variation for a set, type {set|<setName>} "
				+ "and {format|"+StringUtils.join(FORMATS.values(),",")+"} for format shakes.\nPolicy: https://www.mtgcompanion.org/policy.html").queue();

		if(!getString(PRICE_KEYWORDS).isEmpty())
			channel.sendMessage("Also you can type one of this keyword if you want to get prices : " + getString(PRICE_KEYWORDS)+ " like in exemple : \" give me price of {Black Lotus|LEA} \"").queue() ;

	}


	private void responseSearch(MessageReceivedEvent event,String name, DiscordInfo info)
	{
		boolean priceask = !StringUtils.isEmpty(getString(PRICE_KEYWORDS)) && StringUtils.containsAny(event.getMessage().getContentRaw().toLowerCase(), getArray(PRICE_KEYWORDS));
		final List<MTGCard> liste = new ArrayList<>();
		MTGEdition ed = null;
		if(name.contains("|"))
		{
			try {
				ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(name.substring(name.indexOf('|')+1,name.length()).toUpperCase().trim());
			} catch (IOException _) {
				ed = new MTGEdition(name.substring(name.indexOf('|')+1,name.length()).toUpperCase().trim());
			}
			name=name.substring(0, name.indexOf('|')).trim();
		}

		var channel = event.getChannel();
			channel.sendTyping().queue();

			try {
				liste.addAll(getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, ed, false));
			}
			catch(Exception e)
			{
				logger.error(e);
			}

			if(liste.isEmpty())
			{
				channel.sendMessage("Sorry i can't find "+name ).queue();
				return;
			}

			var builder = new NavigableEmbed.Builder(event.getChannel());
			for (var x = 0; x < liste.size(); x++) {
				MTGCard result = liste.get(x);
				BiFunction<MTGCard, Integer, MessageEmbed> getEmbed = (_, resultIndex) -> {
					var embed=parseCard(result,priceask,info);
					var eb = new EmbedBuilder(embed);
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

				var rl = new ReactionListener(jda, navEb.getMessage(), false, 30L * 1000L);
				rl.addController(event.getAuthor());
				rl.addResponse(EmbedButton.PREVIOUS.getIcon(), _ -> {
					navEb.setY(0);
					if (navEb.getX() > 0) navEb.left();
					applyControl(EmbedButton.PREVIOUS.getIcon(), navEb.getMessage(), navEb.getWidth() > 1);
				});
				rl.addResponse(EmbedButton.NEXT.getIcon(), _ -> {
					navEb.setY(0);
					if (navEb.getX() < navEb.getWidth() - 1) navEb.right();
					applyControl(EmbedButton.NEXT.getIcon(), navEb.getMessage(), navEb.getWidth() > 1);
				});

			}

	}


	private void applyControl(String emote, Message message, boolean enabled) {
			try{

				message.addReaction(Emoji.fromFormatted(emote)).queue();
			}
			catch(InsufficientPermissionException ex)
			{
				message.getChannel().sendMessage(ex.getLocalizedMessage() ).queue();
				return;
			}


			if (!enabled) {
				message.getReactions().parallelStream().filter(r -> r.getEmoji().getName().equals(emote))
								   .forEach(r -> {
									   	try {
											r.retrieveUsers().submit().get().parallelStream().forEach(u -> r.removeReaction(u).queue());
										}
									   	catch(InterruptedException _){
									   		Thread.currentThread().interrupt();
									   	}
									   	catch (Exception e) {
											logger.error(e);
										}
								   	});
		}
	}


	private MessageEmbed parseCard(MTGCard mc,boolean price,DiscordInfo info) {

		var eb = new EmbedBuilder();
		eb.setDescription("");
		eb.setTitle(mc.getName()+ " " + (mc.getCost()!=null?mc.getCost():""));
		eb.setColor(EnumColors.determine(mc.getColors()).toColor());

		var temp = new StringBuilder();
		temp.append(mc.getTypes()+"\n");
		temp.append(mc.getText()).append("\n");
		temp.append("**Edition:** ").append(mc.getEdition().getSet()).append("\n");

		if(!getString(EXTERNAL_LINK).isEmpty())
			temp.append("**Url:** ").append(getString(EXTERNAL_LINK)+mc.getScryfallId()).append("\n");

		if(!mc.getExtra().isEmpty())
			temp.append("**").append(mc.getExtra()).append("** ").append("\n");

		temp.append("**Reserved:** ");
		if(mc.isReserved())
			temp.append(":white_check_mark: \n");
		else
			temp.append(":no_entry_sign:  \n");

		if(getBoolean(SHOWCOLLECTIONS)) {
			try {
				temp.append("**Present in:** "+getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc).toString());
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		eb.setDescription(temp.toString());

		if(getString(THUMBNAIL_IMAGE).equalsIgnoreCase(THUMBNAIL))
			eb.setThumbnail(MTG.getEnabledPlugin(MTGPictureProvider.class).generateUrl(mc));
		else
			eb.setImage(MTG.getEnabledPlugin(MTGPictureProvider.class).generateUrl(mc));

		if(price) {


			StringBuilder errMsg = new StringBuilder();

			listEnabledPlugins(MTGPricesProvider.class).forEach(prov->{
				List<MTGPrice> prices = null;

					try {
						prices = prov.getPrice(mc);
						Collections.sort(prices, new MagicPricesComparator());
						if(!prices.isEmpty())
							eb.addField(prov.getName(),UITools.formatDouble(prices.get(0).getValue())+prices.get(0).getCurrency().getCurrencyCode(),true);
					} catch (Exception e) {
						logger.error(e);
						errMsg.append(prov).append(":").append(e);
					}

					try {
						if(prices!=null && !prices.isEmpty()) {
							prices = prices.stream().filter(MTGPrice::isFoil).sorted(new MagicPricesComparator()).toList();
							if(prices!=null && !prices.isEmpty())
								eb.addField(prov.getName() +" foil",UITools.formatDouble(prices.get(0).getValue())+" "+prices.get(0).getCurrency().getCurrencyCode(),true);
						}
					} catch (Exception e) {
						errMsg.append(prov).append(":").append(e);
						logger.error("error on prices",e);
					}


				}
			);

			if(!errMsg.isEmpty())
				info.setError(errMsg.toString());

		}
		return eb.build();
	}

	@Override
	public void start() throws IOException {
		try {
			initListener();
			jda = JDABuilder.createDefault(getAuthenticator().get(TOKEN))
							.addEventListeners(listener)
							.enableIntents(GatewayIntent.MESSAGE_CONTENT)
							.build();

			if(!StringUtils.isEmpty(getString(ACTIVITY_TYPE)) && !StringUtils.isEmpty(getString(ACTIVITY)))
				jda.getPresence().setPresence(Activity.of(ActivityType.valueOf(getString(ACTIVITY_TYPE)), getString(ACTIVITY)), isAlive());



		} catch (Exception e) {
			logger.error(e);
			throw new IOException(e);
		}


	}

	@Override
	public void stop() throws IOException {
		if(jda!=null)
		{
			jda.shutdown();
			jda.getPresence().setPresence(OnlineStatus.OFFLINE,false);

			logger.info("Server {} stopped",getName());
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
		return "Query your  "+MTGConstants.MTG_APP_NAME+"  via discord Bot ";
	}

	@Override
	public String getName() {
		return "Discord";
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(DiscordBotServer.class.getResource("/icons/plugins/discord.png"));
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = new HashMap<String,MTGProperty>();
				map.put(AUTOSTART, MTGProperty.newBooleanProperty(FALSE, "Run bot at startup"));
				map.put(SHOWCOLLECTIONS,MTGProperty.newBooleanProperty(TRUE, "return the collections where the searched card is present"));
				map.put(ACTIVITY_TYPE, new MTGProperty(ActivityType.WATCHING.name(),"The current activity of the bot",ArrayUtils.toStringArray(ActivityType.values())));
				map.put(ACTIVITY, new MTGProperty("bees flying","textual complement of the bot activity"));
				map.put("BLOCKED_USERS",new MTGProperty("","enter here the usernames that are blocked by the bot. The bot will not respond to their queries. Separated by a comma."));
				map.put(EXTERNAL_LINK,new MTGProperty("https://my.mtgcompanion.org/prices-ui/pages/index.html?id=","if you want to redirect the response with a external link. Bot will complete the url with scryfallID"));
				map.put(PRICE_KEYWORDS,new MTGProperty("price,prix,how much,cost","keywords parsed in query message that will activate price search"));
				map.put(THUMBNAIL_IMAGE, new MTGProperty(THUMBNAIL,"how is integrate the card picture in the response",THUMBNAIL,"IMAGE"));
				map.put(RESULTS_SHAKES,MTGProperty.newIntegerProperty("10","the number of max results returned by the bot on cardshakes query",1,-1));
				
		return map;
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
				message = channel.sendMessageEmbeds(embed).submit().get();
			else {
				message = message.editMessageEmbeds(embed).submit().get();
			}
		} catch (InterruptedException | ExecutionException _) {
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

		if (message == null || event.getMessageIdLong() != message.getIdLong() || event.getUser()==null|| !controllers.contains(event.getUser().getId()))
			return;


		ReactionCallback cb = actionMap.getOrDefault(event.getEmoji().getName(), null);

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




