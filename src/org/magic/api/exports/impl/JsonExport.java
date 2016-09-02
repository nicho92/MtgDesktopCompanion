package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MagicFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class JsonExport  extends AbstractCardExport {

	
	public static void main(String[] args) throws Exception {
		JsonExport exp = new JsonExport();
		exp.export(new MTGDesktopCompanionExport().importDeck(new File(MagicFactory.CONF_DIR,"\\decks\\RW Angels.deck")).getAsList(),null);
	}
	
	public JsonExport() {
		super();
	}
	
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		//todo
	}

	@Override
	public String getFileExtension() {
		return ".json";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		//todo
		
	}

	@Override
	public String getName() {
		return "Json";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CSVExport.class.getResource("/res/json.png"));
	}

}
