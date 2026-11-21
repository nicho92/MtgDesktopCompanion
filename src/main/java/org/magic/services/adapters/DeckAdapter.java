package org.magic.services.adapters;

import java.lang.reflect.Type;
import java.util.Date;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class DeckAdapter implements JsonDeserializer<MTGDeck>, JsonSerializer<MTGDeck> {

	private static final String ID = "id";
	private static final String AVERAGE_PRICE = "averagePrice";
	private static final String COMMANDER = "commander";
	private static final String UPDATE_DATE = "updateDate";
	private static final String CREATION_DATE = "creationDate";
	private static final String COLORS = "colors";
	private static final String NAME = "name";
	private static final String TAGS = "tags";
	private static final String DESCRIPTION = "description";
	private final Gson serializer;

	public DeckAdapter() {
		serializer = new Gson();
	}

	@Override
	public MTGDeck deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context)
			throws JsonParseException {
		final var root = elem.getAsJsonObject();

		final var deck = new MTGDeck();

		if (root.get(ID) != null) {
		    deck.setId(root.get(ID).getAsInt());
		}

		if (!root.get(NAME).isJsonNull()) {
		    deck.setName(root.get(NAME).getAsString());
		} else {
		    deck.setName("No Name");
		}

		if (root.get(DESCRIPTION) != null && !root.get(DESCRIPTION).isJsonNull()) {
		    deck.setDescription(root.get(DESCRIPTION).getAsString());
		}

		if (!root.get(CREATION_DATE).isJsonNull()) {
			try {
				deck.setCreationDate(new Date(root.get(CREATION_DATE).getAsLong()));
			} catch (final Exception _) {
				// do nothing
			}
		}

		if (!root.get(UPDATE_DATE).isJsonNull()) {
			try {
				deck.setDateUpdate(new Date(root.get(UPDATE_DATE).getAsLong()));
			} catch (final Exception _) {
				// do nothing
			}

		}

		if (root.get(COMMANDER) != null) {
		    deck.setCommander(serializer.fromJson(root.get(COMMANDER), MTGCard.class));
		}

		if (root.get(AVERAGE_PRICE) != null) {
		    deck.setAveragePrice(root.get(AVERAGE_PRICE).getAsDouble());
		}

		if (!root.get(TAGS).isJsonNull()) {
			final var arr = root.get(TAGS).getAsJsonArray();
			for (var i = 0; i < arr.size(); i++) {
			    deck.getTags().add(arr.get(i).getAsString());
			}
		}

		final var main = root.get("main").getAsJsonArray();

		for (var i = 0; i < main.size(); i++) {
			final var line = main.get(i).getAsJsonObject();
			final var qte = line.get("qty").getAsInt();
			final MTGCard mc = serializer.fromJson(line.get("card"), MTGCard.class);
			deck.getMain().put(mc, qte);
		}

		final var side = root.get("side").getAsJsonArray();

		for (var i = 0; i < side.size(); i++) {
			final var line = side.get(i).getAsJsonObject();
			final var qte = line.get("qty").getAsInt();
			final var mc = serializer.fromJson(line.get("card"), MTGCard.class);
			deck.getSideBoard().put(mc, qte);

		}

		return deck;

	}

	@Override
	public JsonElement serialize(MTGDeck deck, Type typeOfSrc, JsonSerializationContext context) {
		final var json = new JsonObject();
		json.addProperty(ID, deck.getId());
		json.addProperty(NAME, deck.getName());
		json.addProperty(DESCRIPTION, deck.getDescription());
		json.addProperty(COLORS, deck.getColors());
		json.addProperty(AVERAGE_PRICE, deck.getAveragePrice());
		json.add(COMMANDER, serializer.toJsonTree(deck.getCommander()));
		json.addProperty(CREATION_DATE, deck.getDateCreation().getTime());
		json.addProperty(UPDATE_DATE, deck.getDateUpdate().getTime());
		final var tags = new JsonArray();
		for (final String s : deck.getTags()) {
		    tags.add(s);
		}

		json.add(TAGS, tags);

		final var main = new JsonArray();

		for (final MTGCard mc : deck.getMain().keySet()) {
			final var card = new JsonObject();
			card.addProperty("qty", deck.getMain().get(mc));
			card.add("card", serializer.toJsonTree(mc));
			main.add(card);
		}

		final var side = new JsonArray();

		for (final MTGCard mc : deck.getSideBoard().keySet()) {
			final var card = new JsonObject();
			card.addProperty("qty", deck.getSideBoard().get(mc));
			card.add("card", serializer.toJsonTree(mc));
			side.add(card);
		}
		json.add("main", main);
		json.add("side", side);
		return json;
	}

}