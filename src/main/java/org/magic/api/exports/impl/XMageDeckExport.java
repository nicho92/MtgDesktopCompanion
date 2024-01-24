package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class XMageDeckExport extends AbstractFormattedFileCardExport {


	@Override
	public String getName() {
		return "XMage";
	}

	@Override
	public String getFileExtension() {
		return ".dck";
	}


	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		temp.append("NAME: " + deck.getName() + "\n");


		for (MagicCard mc : deck.getMain().keySet().stream().filter(mc->mc!=deck.getCommander()).sorted((mc,mc2)->mc.getName().compareTo(mc2.getName())).toList()) {
			temp.append(deck.getMain().get(mc)).append(" ").append("[").append(mc.getCurrentSet().getId())
					.append(":").append(mc.getNumber()).append("]").append(" ")
					.append(mc.getName()).append("\n");
			notify(mc);
		}
		for (MagicCard mc : deck.getSideBoard().keySet().stream().sorted((mc,mc2)->mc.getName().compareTo(mc2.getName())).toList()) {
			temp.append("SB: ").append(deck.getSideBoard().get(mc)).append(" ").append("[")
					.append(mc.getCurrentSet().getId()).append(":").append(mc.getNumber())
					.append("]").append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}

		if(deck.getCommander()!=null)
		{
			temp.append("SB: ").append("1").append(" ").append("[")
			.append(deck.getCommander().getCurrentSet().getId()).append(":").append(deck.getCommander().getNumber())
			.append("]").append(" ").append(deck.getCommander().getName()).append("\n");
			notify(deck.getCommander());
		}

		FileTools.saveFile(dest, temp.toString());
	}

	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException {
			var deck = new MagicDeck();
			deck.setName(dname);

			matches(f,true).forEach(m->{

				var cname = cleanName(m.group(5));
				MagicEdition ed = null;
				try {
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(m.group(3));
				}
				catch(Exception e)
				{
					logger.error("Edition not found for {}",m.group(3));
				}

				String number=null;
				try {
					number = m.group(4);
				}
				catch(IndexOutOfBoundsException e)
				{
					//do nothing
				}

				MagicCard mc = null;
				if(number!=null && ed !=null)
				{
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);
					} catch (Exception e) {
						logger.error("no card found with number {}/{}",number,ed);
					}
				}

				if(mc==null)
				{
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname, ed,true).get(0);
					} catch (Exception e) {
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
