package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class CardCastleExport extends AbstractCardExport {

	private String header="Count,Card Name,Set Name,Foil";
	private char separator=',';
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder build = new StringBuilder();
		build.append(header).append("\n");
		
		deck.getMap().entrySet().forEach(entry->{
			
			String name = entry.getKey().getName();
			
			if(name.contains(","))
				name="\""+name+"\"";
			
			
			build.append(entry.getValue()).append(separator);
			build.append(name).append(separator);
			build.append(entry.getKey().getCurrentSet().getSet()).append(separator);
			build.append("false").append("\n");
		});
		FileUtils.write(dest, build.toString(),MTGConstants.DEFAULT_ENCODING);
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(name);

		String[] lines = UITools.stringLineSplit(f);
		lines= ArrayUtils.remove(lines,0); // remove header
		
		
		for(String s : lines)
		{
			try {
			String[] columns = s.split(String.valueOf(separator));
			String  cardName = columns[0];
			String editionName = columns[1];
			if(columns[0].contains("\""))
			{
				cardName = String.join(String.valueOf(separator), columns[0],columns[1]);
				cardName = RegExUtils.removeAll(cardName, "\"");
				editionName = columns[2];
			}
			List<MagicCard> cards = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cardName, MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(editionName), true);
			deck.add(cards.get(0));
			}catch(Exception e)
			{
				logger.error("error adding " + s, e);
			}
		}
		
		
		
		
		return deck;
	}

	@Override
	public String getName() {
		return "CardCastle";
	}

}
