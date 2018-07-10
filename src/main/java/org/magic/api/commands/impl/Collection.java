package org.magic.api.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.CommandResponse;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.services.MTGControler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Collection extends AbstractCommand {

	
	@Override
	public void initOptions() {
		opts.addOption("l", "list", false, "list Collections");
		opts.addOption("s", "sets", false, "list editions");
		opts.addOption("?", "help", false, "help for command");
		
	}
	
	@Override
	public CommandResponse<?> run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,ParseException, IOException, InvocationTargetException, NoSuchMethodException {
	
		logger.debug("running "+ this +" with " + Arrays.asList(args));
		CommandLine cl = parser.parse(opts, args);
		if (cl.hasOption("l")) {
			try {
				return new CommandResponse<>(MagicCollection.class, null, json.toJsonElement(MTGControler.getInstance().getEnabledDAO().getCollections()));
			} catch (SQLException e) {
				return null;
			}
		}
		
		if (cl.hasOption("s")) {
			List<MagicEdition> eds = MTGControler.getInstance().getEnabledCardsProviders().loadEditions();
			MagicEditionsTableModel model = new MagicEditionsTableModel();
			model.init(eds);
			double pc=0;
			JsonArray arr =new JsonArray();
			for (MagicEdition ed : eds) {
				JsonObject obj = new JsonObject();
				obj.add("edition", new Gson().toJsonTree(ed));
				obj.addProperty("release", ed.getReleaseDate());
				obj.add("qty", new JsonPrimitive(model.getMapCount().get(ed)));
				obj.add("cardNumber", new JsonPrimitive(ed.getCardCount()));
				obj.addProperty("defaultLibrary", MTGControler.getInstance().get("default-library"));
				if (ed.getCardCount() > 0)
					pc = (double) model.getMapCount().get(ed) / ed.getCardCount();
				else
					pc = (double) model.getMapCount().get(ed) / 1;

				obj.add("pc", new JsonPrimitive(pc*100));

				arr.add(obj);
			}
			return new CommandResponse<>(JsonArray.class, Arrays.asList("edition","release","qty","cardNumber","defaultLibrary","pc"), arr);
		}
		
		if (cl.hasOption("l")) {
			try {
				return new CommandResponse<>(MagicCollection.class, null, json.toJsonElement(MTGControler.getInstance().getEnabledDAO().getCollections()));
			} catch (SQLException e) {
				return null;
			}
		}
		if (cl.hasOption("?")) {
			return usage();
		}
		
		return null;
	}

	
}
