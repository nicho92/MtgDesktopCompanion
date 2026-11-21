package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.tools.MTG;

import com.google.gson.JsonObject;

public class TCGPlayerDeckSniffer extends AbstractDeckSniffer {

    private static final String DECK_DATA = "deckData";
    private static final String CARDS = "cards";
    private static final String SUB_DECKS = "subDecks";

    @Override
    public String[] listFilter() {
	return new String[]{"standard", "modern", "legacy", "vintage", "commander", "pioneer", "pauper", "historic", "brawl"};
    }

    @Override
    public MTGDeck getDeck(RetrievableDeck info) throws IOException {
	logger.debug("get deck at {}", info.getUrl());
	final MTGDeck deck = info.toBaseDeck();

	final var obj = RequestBuilder.build().newClient().get().url(info.getUrl())
		.addContent("source", "infinite-content")
		.addContent(SUB_DECKS, "true")
		.addContent(CARDS, "true")
		.addContent("stats", "false")
		.toJson().getAsJsonObject().get("result").getAsJsonObject();

	deck.setDescription("Imported from https://www.tcgplayer.com/content"+obj.get("canonicalURL").getAsString());

	fillData(obj, "maindeck", deck.getMain());
	fillData(obj, "sideboard", deck.getSideBoard());

	try {
	    final var cmd = obj.get("deck").getAsJsonObject().get(SUB_DECKS).getAsJsonObject().get("commandzone");
	    if(cmd!=null)
	    {
		final var id = cmd.getAsJsonArray().get(0).getAsJsonObject().get("cardID").getAsString();
		deck.setCommander(parseCard(obj.get(CARDS).getAsJsonObject().get(id).getAsJsonObject()));
		deck.getMain().put(deck.getCommander(), 1);
	    }
	}
	catch(final Exception _)
	{
	    logger.error("errr getting commander");
	}


	return deck;

    }

    private void fillData(JsonObject obj, String side, Map<MTGCard,Integer> data)
    {
	if( obj.get("deck").getAsJsonObject().get(SUB_DECKS).getAsJsonObject().get(side)==null) {
	    return;
	}

	obj.get("deck").getAsJsonObject().get(SUB_DECKS).getAsJsonObject().get(side).getAsJsonArray().forEach(je->{

	    final var id= je.getAsJsonObject().get("cardID").getAsString();
	    final var cardData = obj.get(CARDS).getAsJsonObject().get(id).getAsJsonObject();
	    final var qty = je.getAsJsonObject().get("quantity").getAsInt();

	    final var c = parseCard(cardData);

	    if(c!=null) {
		data.put(c, qty);
            }

	});
    }


    private MTGCard parseCard(JsonObject obj)  {

	final var name = obj.get("name").getAsString();
	final var set = obj.get("set").getAsString();
	try {
	    final var edition = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(set);
	    final var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(obj.get("name").getAsString(), edition, true).getFirst();
	    notify(mc);
	    return mc;

	} catch (final Exception _) {
	    logger.error("can't find card for {}/{}",name,set);
	}

	return null;
    }

    @Override
    public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {

	final var list = new ArrayList<RetrievableDeck>();

	final var obj = RequestBuilder.build().newClient().get().url("https://infinite-api.tcgplayer.com/content/decks/magic")
		.addContent("source", "infinite-content")
		.addContent("rows", "128")
		.addContent("format", filter)
		.addContent("isAdmin", "false")
		.addContent("td", "false")
		.addContent("sort", "latest")
		.addContent("offset", "0")
		.addContent("order", "desc").toJson().getAsJsonObject();


	for(final var d : obj.get("result").getAsJsonArray())
	{
	    final var deck = new RetrievableDeck();

	    deck.setAuthor(d.getAsJsonObject().get(DECK_DATA).getAsJsonObject().get("playerName").getAsString());
	    deck.setDescription(d.getAsJsonObject().get("date").getAsString());
	    deck.setName(d.getAsJsonObject().get(DECK_DATA).getAsJsonObject().get("deckName").getAsString());
	    deck.setUrl(URI.create("https://infinite-api.tcgplayer.com/deck/magic/"+d.getAsJsonObject().get("deckID").getAsString()));

	    if(d.getAsJsonObject().get(DECK_DATA).getAsJsonObject().get("colors")!=null) {
		deck.setColor(Arrays.stream(d.getAsJsonObject().get(DECK_DATA).getAsJsonObject().get("colors").getAsString().split(",")).map(s -> "{" + s + "}").collect(Collectors.joining()));
	    }

	    list.add(deck);
	}
	return list;
    }

    @Override
    public String getName() {
	return "TCGPlayer";
    }

}
