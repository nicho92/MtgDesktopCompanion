package org.beta;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicCard;
import org.magic.console.MTGConsoleHandler;
import org.magic.console.commands.Get;
import org.magic.console.commands.Search;
import org.magic.services.MTGControler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class AsciiTableTest {

	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		
		JsonElement el = new Search().run(new String[] {"search","-c","name=liliana"});
		MTGConsoleHandler handler = new MTGConsoleHandler();
		System.out.println(handler.showList(el.getAsJsonArray()));
	}
}
