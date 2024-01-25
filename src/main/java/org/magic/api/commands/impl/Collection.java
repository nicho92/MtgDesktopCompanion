package org.magic.api.commands.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.services.MTGControler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Collection extends AbstractCommand {


	@Override
	public void initOptions() {
		super.initOptions();
		opts.addOption("l", "list", false, "list Collections");
		opts.addOption("s", "sets", false, "list editions");
	}

	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,ParseException, IOException, InvocationTargetException, NoSuchMethodException {

		logger.debug("running {} with {}", this,Arrays.asList(args));
		CommandLine cl = parser.parse(opts, args);
		if (cl.hasOption("l")) {
			try {
				return new ArrayResponse(MTGCollection.class, null, json.toJsonArray(getEnabledPlugin(MTGDao.class).listCollections()));
			} catch (SQLException e) {
				return null;
			}
		}

		if (cl.hasOption("s")) {
			List<MTGEdition> eds = getEnabledPlugin(MTGCardsProvider.class).listEditions();
			var model = new MagicEditionsTableModel();
			model.init(eds);
			double pc=0;
			var arr =new JsonArray();
			for (MTGEdition ed : eds) {
				var obj = new JsonObject();
				obj.addProperty("edition", ed.getSet());
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
			return new ArrayResponse(JsonArray.class, Arrays.asList("edition","release","qty","cardNumber","defaultLibrary","pc"), arr);
		}

		if (cl.hasOption("l")) {
			try {
				return new ArrayResponse(MTGCollection.class, null, json.toJsonArray(getEnabledPlugin(MTGDao.class).listCollections()));
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
