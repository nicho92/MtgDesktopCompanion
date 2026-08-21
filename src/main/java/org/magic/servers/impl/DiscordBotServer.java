package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGFormat.FORMATS;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.audit.MessageInfo;
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
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import com.github.ygimenez.model.PaginatorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class DiscordBotServer extends AbstractMTGServer {


  
    private static final String COMMAND_FORMAT = "format";
    private static final String COMMAND_CARD = "card";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_SET = "set";

    private static final String COMMAND_OPTION_FORMATNAME = "formatname";
    private static final String COMMAND_OPTION_CARDNAME = "cardname";
    private static final String COMMAND_OPTION_SETNAME = "setname";
    private static final String COMMAND_OPTION_PRICE = "price";

    private static final String THUMBNAIL = "THUMBNAIL";
    private static final String EXTERNAL_LINK = "EXTERNAL_LINK";
    private static final String ACTIVITY = "ACTIVITY";
    private static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    private static final String THUMBNAIL_IMAGE = "THUMBNAIL_IMAGE";
    private static final String AUTOSTART = "AUTOSTART";
    private static final String TOKEN = "TOKEN";
    private static final String SHOWCOLLECTIONS = "SHOW_COLLECTIONS";
    private static final String AUTOCOMPLETE_START = "AUTOCOMPLETE_START";
    private static final String CARD_SHAKE_LIMIT = "CARD_SHAKE_LIMIT";
    private static final int RESULT_LIMIT=25;

    private JDA jda;

    @Override
    public String getVersion() {
	return JDAInfo.VERSION;
    }

    private void parseInteractionEvent(SlashCommandInteractionEvent event) {

	var info = new MessageInfo();
	info.setSource(getName());
	info.setUser(parse(event.getUser()));
	info.setChannel(parse(event.getChannel()));
	info.setGuild(parse(event.getGuild()));

	switch (event.getName()) {
	case COMMAND_HELP : responseHelp(event,info);break;
	case COMMAND_CARD : responseSearch(event,info);break;
	case COMMAND_FORMAT: reponseFormatSearch(event,info);break;
	case COMMAND_SET: reponseSetSearch(event,info);break;
	}

	info.setEnd(Instant.now());
	AbstractTechnicalServiceManager.inst().store(info);

    }

    @SuppressWarnings("null")
    private void parseAutoComplete(CommandAutoCompleteInteractionEvent event) {

	if(!event.getFocusedOption().getValue().isEmpty() && event.getFocusedOption().getName().equals(COMMAND_OPTION_CARDNAME) && event.getFocusedOption().getValue().length()>=getInt(AUTOCOMPLETE_START))
	{
	    try {

		var results = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(event.getFocusedOption().getValue(),null,false).stream()
			.map(MTGCard::getName)
			.distinct()
			.limit(RESULT_LIMIT)
			.map(s->new Command.Choice(s, s))
			.toList();

		event.replyChoices(results).queue();
	    } catch (Exception e) {
		logger.error(e);
	    }
	    return;
	}


	if(event.getFocusedOption().getName().equals(COMMAND_OPTION_SETNAME))
	{
	    try {
		Stream<MTGEdition> sets = Stream.of();

		if(event.getOption(COMMAND_OPTION_CARDNAME)!=null)
		    sets=getEnabledPlugin(MTGCardsProvider.class).searchCardByName(event.getOption(COMMAND_OPTION_CARDNAME).getAsString(),null,false).getFirst().getEditions().stream();
		else
		    sets=getEnabledPlugin(MTGCardsProvider.class).listEditions().stream();

		var options = sets
			.map(MTGEdition::getSet)
			.distinct()
			.filter(set->set.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
			.limit(RESULT_LIMIT)
			.map(s->new Command.Choice(s, s))
			.toList();

		event.replyChoices(options).queue();

	    } catch (Exception e) {
		logger.error(e);
	    }
	    return;
	}

	if(event.getFocusedOption().getName().equals(COMMAND_OPTION_FORMATNAME))
	{

	    try {
		var options = Stream.of(MTGFormat.FORMATS.values())
			.filter(set->set.name().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
			.limit(RESULT_LIMIT)
			.map(s->new Command.Choice(s.name(), s.name()))
			.toList();

		event.replyChoices(options).queue();

	    } catch (Exception e) {
		logger.error(e);
	    }
	    return;
	}


    }

    @SuppressWarnings("null")
    private void reponseSetSearch(SlashCommandInteractionEvent event, MessageInfo info) {

	info.setMessage("/"+COMMAND_SET + " " + event.getOption(COMMAND_OPTION_SETNAME));
	event.deferReply().queue();
	var eb = new EmbedBuilder();
	eb.setTitle(event.getOption(COMMAND_OPTION_SETNAME).getAsString());
	try {
	    var ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(event.getOption(COMMAND_OPTION_SETNAME).getAsString());
	    var results = MTG.getEnabledPlugin(MTGDashBoard.class).getShakesForEdition(ed).getShakes();


	    var up = results.stream().sorted(new PricesCardsShakeSorter(SORT.DAY_PERCENT_CHANGE,false)).limit(getInt(CARD_SHAKE_LIMIT));
	    var down = results.stream().sorted(new PricesCardsShakeSorter(SORT.DAY_PERCENT_CHANGE,true)).limit(getInt(CARD_SHAKE_LIMIT));

	    Stream.concat(up, down).forEach(cs->{
		var icon = ":heavy_equals_sign: "; 
		if(cs.getPercentDayChange()>0)
		    icon = ":arrow_upper_right: ";
		else if(cs.getPercentDayChange()<0)
		    icon = ":arrow_lower_right: ";

		eb.addField(cs.getName(), icon+ UITools.formatDouble(cs.getPercentDayChange()*100)+"% "+ " : " +  cs.getPrice(),false);
	    });

	} catch (Exception e) {
	    logger.error(e);
	}

	event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    @SuppressWarnings("null")
    private void reponseFormatSearch(SlashCommandInteractionEvent event, MessageInfo info) {

	info.setMessage("/"+COMMAND_FORMAT + " " + event.getOption(COMMAND_OPTION_FORMATNAME));
	event.deferReply().queue();
	var eb = new EmbedBuilder();
	eb.setTitle(event.getOption(COMMAND_OPTION_FORMATNAME).getAsString());
	try {

	    var results = MTG.getEnabledPlugin(MTGDashBoard.class).getShakerFor(FORMATS.valueOf(event.getOption(COMMAND_OPTION_FORMATNAME).getAsString()));


	    var up = results.stream().sorted(new PricesCardsShakeSorter(SORT.DAY_PERCENT_CHANGE,false)).limit(getInt(CARD_SHAKE_LIMIT));
	    var down = results.stream().sorted(new PricesCardsShakeSorter(SORT.DAY_PERCENT_CHANGE,true)).limit(getInt(CARD_SHAKE_LIMIT));

	    Stream.concat(up, down).forEach(cs->{
		var icon = ":heavy_equals_sign: "; 
		if(cs.getPercentDayChange()>0)
		    icon = ":arrow_upper_right: ";
		else if(cs.getPercentDayChange()<0)
		    icon = ":arrow_lower_right: ";

		try {
		    var ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(cs.getEd());

		    eb.addField(cs.getName() + " ("+ed+")", icon+ UITools.formatDouble(cs.getPercentDayChange()*100)+"% "+ " : " +  cs.getPrice(),false);

		} catch (Exception e) {
		    eb.addField(cs.getName() + " ("+cs.getEd()+")", icon+ UITools.formatDouble(cs.getPercentDayChange()*100)+"% "+ " : " +  cs.getPrice(),false);
		}
	    });

	} catch (Exception e) {
	    logger.error(e);
	}

	event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private void responseHelp(SlashCommandInteractionEvent event, MessageInfo info) {

	info.setMessage("/"+COMMAND_HELP);
	event.reply(":face_with_monocle: It's simple \n"
		+ "/card command with cardname. You can complet with setname value to filter the set. You can set 'price' to True, if you want to get prices of the card\n"
		+ "/format get prices shakers for the selected format and return "+getInt(CARD_SHAKE_LIMIT)+ "biggest and "+getInt(CARD_SHAKE_LIMIT) + "lowest movements\n"
		+ "/set get prices shakers for the selected set and return "+getInt(CARD_SHAKE_LIMIT)+ "biggest and "+getInt(CARD_SHAKE_LIMIT) + "lowest movements\n")
	.queue();

    }

    @SuppressWarnings("null")
    private void responseSearch(SlashCommandInteractionEvent event, MessageInfo info) {

	info.setMessage("/"+COMMAND_CARD + " {" + event.getOption(COMMAND_OPTION_CARDNAME).getAsString() +"} :  " + event.getOption(COMMAND_OPTION_SETNAME));
	event.deferReply().queue();


	final List<MTGCard> liste = new ArrayList<>();

	var price = event.getOption(COMMAND_OPTION_PRICE)!=null?event.getOption(COMMAND_OPTION_PRICE).getAsBoolean():false;

	try {
	    MTGEdition ed = null;
	    if(event.getOption(COMMAND_OPTION_SETNAME)!=null)
		ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(event.getOption(COMMAND_OPTION_SETNAME).getAsString());

	    liste.addAll(getEnabledPlugin(MTGCardsProvider.class).searchCardByName(event.getOption(COMMAND_OPTION_CARDNAME).getAsString(), ed, false));

	} catch (Exception e) {
	    logger.error(e);
	}

	if (liste.isEmpty()) {
	    event.reply("Sorry i can't find " + event.getOption(COMMAND_OPTION_CARDNAME).getAsString()).queue();
	    return;
	}

	var pages = new ArrayList<Page>();
	pages.addAll(liste.stream().map(c->InteractPage.of(createCardEmbedMessage(c, price, info))).toList());

	event.getHook().sendMessageEmbeds(createCardEmbedMessage(liste.getFirst(), price, info)).queue(success->{
	    if(liste.size()>1)
		Pages.paginate(success, pages, true);
	});


    }

    @SuppressWarnings("null")
    private MessageEmbed createCardEmbedMessage(MTGCard mc, boolean price, MessageInfo info) {

	var eb = new EmbedBuilder();

	eb.setTitle(mc.getName() + " " + (mc.getCost() != null ? mc.getCost() : ""));
	eb.setColor(EnumColors.determine(mc.getColors()).toColor());
	eb.addField("**Type**",mc.getFullType(),true);
	eb.addField("**Oracle**",mc.getText(),false);
	eb.addField("**Set**",mc.getEdition().getSet(),true);
	eb.addField("**Reserved**",mc.isReserved()?":white_check_mark:":":no_entry_sign:",true);

	if(!mc.getExtra().isEmpty())
	    eb.addField("**Extra**",mc.getExtra().toString().toLowerCase(),true);


	if (getBoolean(SHOWCOLLECTIONS)) {
	    try {
		eb.addField("**Collections**",getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc).toString(),false);
	    } catch (SQLException e) {
		logger.error(e);
	    }
	}

	if (getString(THUMBNAIL_IMAGE).equalsIgnoreCase(THUMBNAIL))
	    eb.setThumbnail(MTG.getEnabledPlugin(MTGPictureProvider.class).generateUrl(mc, false));
	else
	    eb.setImage(MTG.getEnabledPlugin(MTGPictureProvider.class).generateUrl(mc, false));

	if (price) 
	{

	    var errMsg = new StringBuilder();

	    listEnabledPlugins(MTGPricesProvider.class).forEach(prov -> {
		List<MTGPrice> prices = null;
		try {
		    prices = prov.getPrice(mc);
		    Collections.sort(prices, new MagicPricesComparator());
		    if (!prices.isEmpty())
			eb.addField(prov.getName(), UITools.formatDouble(prices.getFirst().getValue())+ prices.getFirst().getCurrency().getCurrencyCode(), true);

		} catch (Exception e) {
		    logger.error(e);
		    errMsg.append(prov).append(":").append(e);
		}

		try {
		    if (prices != null && !prices.isEmpty()) {
			prices = prices.stream().filter(MTGPrice::isFoil).sorted(new MagicPricesComparator()).toList();
			if (prices != null && !prices.isEmpty())
			    eb.addField(prov.getName() + " foil", UITools.formatDouble(prices.getFirst().getValue()) + " " + prices.getFirst().getCurrency().getCurrencyCode(), true);
		    }
		} catch (Exception e) {
		    errMsg.append(prov).append(":").append(e);
		    logger.error("error on prices", e);
		}

	    });

	    if (!errMsg.isEmpty())
		info.setError(errMsg.toString());

	}
	if(!getString(EXTERNAL_LINK).isEmpty())
	    eb.setUrl(getString(EXTERNAL_LINK)+mc.getScryfallId());

	return eb.build();
    }

    @SuppressWarnings("null")
    @Override
    public void start() throws IOException {
	try {
	    jda = JDABuilder.createDefault(getAuthenticator().get(TOKEN))
		    .addEventListeners(new ListenerAdapter() {
			@Override
			public void onReady(@Nonnull ReadyEvent event) {
			    logger.info("Server {} started", getName());
			}
			@Override
			public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
			    logger.info("getting commands {} : {}",event.getFullCommandName(),event.getOptions());
			    parseInteractionEvent(event);
			}
			@Override
			public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
			    parseAutoComplete(event);
			}
		    }).build();

	    if (!StringUtils.isEmpty(getString(ACTIVITY_TYPE)) && !StringUtils.isEmpty(getString(ACTIVITY)))
		jda.getPresence().setPresence(Activity.of(ActivityType.valueOf(getString(ACTIVITY_TYPE)), getString(ACTIVITY)), isAlive());

	    initCommands();

	    Pages.activate(PaginatorBuilder.createSimplePaginator(jda));


	} catch (Exception e) {
	    logger.error(e);
	    throw new IOException(e);
	}

    }

    private void initCommands() {
	var commands = jda.updateCommands();
	commands.addCommands(
		Commands.slash(COMMAND_CARD, "get card information")
		.addOption(OptionType.STRING, COMMAND_OPTION_CARDNAME, "the card name", true,true)
		.addOption(OptionType.STRING, COMMAND_OPTION_SETNAME, "the set code", false,true)
		.addOption(OptionType.BOOLEAN, COMMAND_OPTION_PRICE, "return prices", false),

		Commands.slash(COMMAND_FORMAT, "get cards shake by format")
		.addOption(OptionType.STRING, COMMAND_OPTION_FORMATNAME, "the selected format", true,true),

		Commands.slash(COMMAND_SET, "get cards shake by set")
		.addOption(OptionType.STRING, COMMAND_OPTION_SETNAME, "the selected set", true,true),

		Commands.slash(COMMAND_HELP, "get help to command")
		);
	commands.queue();

    }

    @Override
    public void stop() throws IOException {
	if (jda != null) {
	    jda.shutdown();
	    jda.getPresence().setPresence(OnlineStatus.OFFLINE, false);
	    logger.info("Server {} stopped", getName());
	}
    }

    @Override
    public boolean isAlive() {
	if (jda != null)
	    return jda.getStatus().equals(JDA.Status.CONNECTED);
	return false;
    }

    @Override
    public boolean isAutostart() {
	return getBoolean(AUTOSTART);
    }

    @Override
    public String description() {
	return "Query your  " + MTGConstants.MTG_APP_NAME + "  via discord Bot ";
    }

    @Override
    public String getName() {
	return "Discord";
    }

    @SuppressWarnings("null")
    @Override
    public Map<String, MTGProperty> getDefaultAttributes() {
	var map = new HashMap<String, MTGProperty>();
	map.put(AUTOSTART, MTGProperty.newBooleanProperty(FALSE, "Run bot at startup"));
	map.put(SHOWCOLLECTIONS, MTGProperty.newBooleanProperty(FALSE, "return the collections where the searched card is present"));
	map.put(ACTIVITY_TYPE, new MTGProperty(ActivityType.WATCHING.name(), "The current activity of the bot", Arrays.stream(ActivityType.values()).map(Enum::name).toList().toArray(new String[0])));
	map.put(ACTIVITY, new MTGProperty("bees flying", "textual complement of the bot activity"));
	map.put(AUTOCOMPLETE_START, MTGProperty.newIntegerProperty("3", "Start autocomplete cardname search when user typed x character", 1, -1));
	map.put(CARD_SHAKE_LIMIT, MTGProperty.newIntegerProperty("5", "number of results to return when calling for shakers", 1, 10));
	map.put(EXTERNAL_LINK, new MTGProperty("https://my.mtgcompanion.org/prices-ui/pages/index.html?id=","if you want to redirect the response with a external link. Bot will complete the url with scryfallID"));
	map.put(THUMBNAIL_IMAGE, new MTGProperty(THUMBNAIL, "how is integrate the card picture in the response", THUMBNAIL, "IMAGE"));

	return map;
    }

    @SuppressWarnings("null")
    public JsonObject toJsonDetails() {
	var jo = new JsonObject();
	if (isAlive()) {

	    var arrGuilds = new JsonArray();
	    jda.getGuilds().forEach(g -> arrGuilds.add(parse(g)));
	    jo.add("guilds", arrGuilds);
	    jo.add("user", parse(jda.getSelfUser()));
	    try {
		jo.addProperty("presenceActivity", String.valueOf(jda.getPresence().getActivity()));
		jo.addProperty("presenceValue", jda.getPresence().getActivity().getName());
	    } catch (Exception e) {
		logger.error(e);
	    }

	}

	return jo;
    }

    private JsonObject parse(User author) {
	var user = new JsonObject();
	user.addProperty("id", author.getId());
	user.addProperty("name", author.getName());
	user.addProperty("mention", author.getAsMention());
	user.addProperty("avatar", author.getAvatarUrl());
	return user;
    }

    private JsonObject parse(Guild g) {

	if(g==null)
	    return null;

	var guild = new JsonObject();
	guild.addProperty("id", g.getId());
	guild.addProperty("banner", g.getBannerUrl());
	guild.addProperty("icon", g.getIconUrl());
	guild.addProperty("name", g.getName());
	guild.addProperty("description", g.getDescription());
	guild.addProperty("etest", g.getVanityUrl());
	return guild;
    }

    private JsonObject parse(MessageChannel c) {
	var channel = new JsonObject();
	channel.addProperty("name", c.getName());
	channel.addProperty("id", c.getId());
	channel.addProperty("type", c.getType().toString());
	return channel;
    }
}
