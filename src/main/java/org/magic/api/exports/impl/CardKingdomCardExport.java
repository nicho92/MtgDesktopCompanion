package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.MTGExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class CardKingdomCardExport extends AbstractCardExport {

	private static final String BASE_URL="https://www.cardkingdom.com/builder";

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}

	@Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}

	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}

	@Override
	public MTGExportCategory getCategory() {
		return MTGExportCategory.ONLINE;
	}


	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();

		var s = BASE_URL+"?partner=Mtgdesktopcompanion&utm_source=Mtgdesktopcompanion&utm_medium=affiliate&utm_campaign=Mtgdesktopcompanion&c=";

		for(Entry<MagicCard, Integer> e : deck.getMain().entrySet())
			temp.append(e.getValue()).append(" ").append(e.getKey()).append("||");


		s = s + URLTools.encode(temp.toString());

		UITools.browse(s);

	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not Implemented");
	}

	@Override
	public String getName() {
		return "Card Kingdom";
	}

	@Override
	public boolean isPartner() {
		return true;
	}

}
