package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class XMageDeckExport extends AbstractFormattedFileCardExport {


	@Override
	public String getName() {
		return "XMage";
	}

	@Override
	public String getStockFileExtension() {
		return ".dck";
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		temp.append("NAME: " + deck.getName() + "\n");


		for (MTGCard mc : deck.getMain().keySet().stream().filter(mc->mc!=deck.getCommander()).sorted((mc,mc2)->mc.getName().compareTo(mc2.getName())).toList()) {
			temp.append(deck.getMain().get(mc)).append(" ").append("[").append(mc.getEdition().getId())
					.append(":").append(mc.getNumber()).append("]").append(" ")
					.append(mc.getName()).append("\n");
			notify(mc);
		}
		for (MTGCard mc : deck.getSideBoard().keySet().stream().sorted((mc,mc2)->mc.getName().compareTo(mc2.getName())).toList()) {
			temp.append("SB: ").append(deck.getSideBoard().get(mc)).append(" ").append("[")
					.append(mc.getEdition().getId()).append(":").append(mc.getNumber())
					.append("]").append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}

		if(deck.getCommander()!=null)
		{
			temp.append("SB: ").append("1").append(" ").append("[")
			.append(deck.getCommander().getEdition().getId()).append(":").append(deck.getCommander().getNumber())
			.append("]").append(" ").append(deck.getCommander().getName()).append("\n");
			notify(deck.getCommander());
		}

		FileTools.saveFile(dest, temp.toString());
	}

	@Override
	public MTGDeck importDeck(String f,String dname) throws IOException {
			var deck = new MTGDeck();
			deck.setName(dname);

			matches(f,true).forEach(m->{

				var cname = cleanName(m.group(5));
				MTGEdition ed = null;
				try {
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(m.group(3));
				}
				catch(Exception _)
				{
					logger.error("Edition not found for {}",m.group(3));
				}

				String number=null;
				try {
					number = m.group(4);
				}
				catch(IndexOutOfBoundsException _)
				{
					//do nothing
				}

				MTGCard mc = null;
				if(number!=null && ed !=null)
				{
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);
					} catch (Exception _) {
						logger.error("no card found with number {}/{}",number,ed);
					}
				}

				if(mc==null)
				{
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname, ed,true).get(0);
					} catch (Exception _) {
						logger.error("no card found for {}/{}",cname,ed);
					}

				}


				if(m.group(1)!=null)
				{
					deck.getSideBoard().put(mc, Integer.parseInt(m.group(2)));
				}
				else
				{
					deck.getMain().put(mc, Integer.parseInt(m.group(2)));
				}

				if(deck.getSideAsList().size()==1 && deck.getMainAsList().size()>=99)
				{
					var card = deck.getSideAsList().get(0);
					deck.getMain().put(card, 1);
					deck.setCommander(card);
					deck.getSideBoard().clear();
				}
				notify(mc);
			});

			return deck;


	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"LAYOUT ","NAME:"};
	}

	@Override
	protected String getSeparator() {
		return null;
	}


}
