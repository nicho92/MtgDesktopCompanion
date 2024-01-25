package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTGArenaTools;

import com.google.gson.JsonObject;

public class MTGArenaImporter extends AbstractCardExport {

	private static final String ARENA_LOG_FILE = "ARENA_LOG_FILE";

	@Override
	public String getFileExtension() {
		return ".txt";
	}

	@Override
	public MODS getMods() {
		return MODS.IMPORT;
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}


	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		throw new IOException("Not implemented");

	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		return MTGDeck.toDeck(importStock(f).stream().map(MTGCardStock::getProduct).toList());
	}



	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> ret = new ArrayList<>();

		JsonObject o = new MTGArenaTools(getFile(ARENA_LOG_FILE)).readCollection();

		o.get("payload").getAsJsonObject().entrySet().forEach(e->{

			String id=e.getKey();
			Integer qty = e.getValue().getAsInt();

			try {
				MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByArenaId(id);

				if(mc!=null)
				{

					MTGCardStock mcs = MTGControler.getInstance().getDefaultStock();

					mcs.setCondition(EnumCondition.ONLINE);
					mcs.setProduct(mc);
					mcs.setQte(qty);

					ret.add(mcs);
					notify(mc);
				}


			} catch (IOException e1) {
				logger.error(e1);
			}


		});
		return ret;
	}


	@Override
	public String getName() {
		return "MTGArena Collection";
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGArenaExport.class.getResource("/icons/plugins/mtgarena.png"));
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(ARENA_LOG_FILE,"C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\LocalLow\\Wizards Of The Coast\\MTGA\\Player.log");
	}

}
