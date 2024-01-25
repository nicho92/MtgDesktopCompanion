package org.magic.api.exports.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.api.cardtrader.services.CardTraderConstants;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class CardTraderWantListExport extends AbstractCardExport {

	@Override
	public String getFileExtension() {
		return ".ctwantlist";
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
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
	}


	@Override
	public String getVersion() {
		return CardTraderConstants.CARDTRADER_JAVA_API_VERSION;
	}


	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {

		var temp = new StringBuilder();

			for (var entry : deck.getMain().entrySet().stream().filter(e->e.getKey().getSide().equalsIgnoreCase("a")).toList()) {
					temp.append(entry.getValue()).append(" ").append(entry.getKey().getFullName()).append(" (").append(getSetId(entry.getKey())).append(")").append(System.lineSeparator());

					notify(entry.getKey());
			}

			for (var entry : deck.getSideBoard().entrySet().stream().filter(e->e.getKey().getSide().equalsIgnoreCase("a")).toList()) {
				temp.append(entry.getValue()).append(" ").append(entry.getKey().getFullName()).append(" (").append(getSetId(entry.getKey())).append(")").append(System.lineSeparator());
				notify(entry.getKey());
			}

			StringSelection selection = new StringSelection(temp.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}



	private String getSetId(MTGCard card) {

		if(card.isExtraCard())
			return "C"+card.getCurrentSet().getId();

		return card.getCurrentSet().getId();



	}




	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not implemented");
	}

	@Override
	public String getName() {
		return CardTraderConstants.CARDTRADER_NAME + " WantList";
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/icons/plugins/"+CardTraderConstants.CARDTRADER_NAME.toLowerCase()+".png"));
	}

}
