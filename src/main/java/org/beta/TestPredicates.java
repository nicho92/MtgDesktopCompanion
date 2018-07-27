package org.beta;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static com.jayway.jsonpath.JsonPath.parse;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.factories.AbilitiesFactory;

import com.google.gson.JsonArray;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class TestPredicates {

	
	public static void main(String[] args) throws IOException {
		File f = new File("C:\\Users\\Pihen\\.magicDeskCompanion\\cardsProviders\\AllSets-x.json");
		CacheProvider.setCache(new LRUCache(400));
		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new GsonJsonProvider();
			private final MappingProvider mappingProvider = new GsonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}

		});
		Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		Filter cheapFictionFilter = filter(where("name").regex(Pattern.compile("/^.*liliana.*$/i")));
		
		System.out.println(cheapFictionFilter);
		
		JsonArray books = parse(f).read("$..cards[?]",cheapFictionFilter);
		
		MagicCard mc = new MagicCard();
		mc.setText(books.get(0).getAsJsonObject().get("text").getAsString());
		
		
		System.out.println(AbilitiesFactory.getInstance().getAbilities(mc));
		
		
	}
}
