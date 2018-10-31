package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class MTGODeckExport extends AbstractCardExport {


	@Override
	public String getName() {
		return "MTGO";
	}

	@Override
	public String getFileExtension() {
		return ".dek";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();

		temp.append("//NAME: " + deck.getName() + " from MTGDeskTopCompanion\n");
		temp.append("\n//MAIN\n");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append(deck.getMap().get(mc)).append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}
		temp.append("\nSideboard\n");
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append(deck.getMapSideBoard().get(mc)).append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}

		FileUtils.writeStringToFile(dest, temp.toString(), MTGConstants.DEFAULT_ENCODING);
	}
	
	
	@Override
	public MagicDeck importDeck(String f, String deckName) throws IOException {
			MagicDeck deck = new MagicDeck();
			deck.setName(deckName);
			boolean side=false;
			for(String line : UITools.stringLineSplit(f)) 
			{
				if (!line.startsWith("//") && line.length() > 0) {
					int sep = line.indexOf(' ');
					if (line.toLowerCase().startsWith("sideboard"))
					{
						side=true;
					}
					else if (side) {
						String name = line.substring(sep, line.length()).trim();
						String qte = line.substring(0, sep).trim();
						
						if(name.indexOf("//")>-1)
							name=name.substring(0, name.indexOf("//")).trim();
						
						sep = line.indexOf(' ');
						name = line.substring(sep, line.length()).trim();
						qte = line.substring(0, sep).trim();
						List<MagicCard> list = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( name, null, (name.indexOf("//")==-1));
						deck.getMapSideBoard().put(list.get(0), Integer.parseInt(qte));
						notify(list.get(0));
					} else {
						String name = line.substring(sep, line.length()).trim().trim();
						String qte = line.substring(0, sep).trim();
						if(name.indexOf("//")>-1)
							name=name.substring(0, name.indexOf("//"));
						
						
						List<MagicCard> list = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( name, null, (name.indexOf("//")==-1));
						deck.getMap().put(list.get(0), Integer.parseInt(qte));
						notify(list.get(0));
					}
				}
			}
			return deck;
		

	}

	@Override
	public void initDefault() {
		setProperty("VERSION", "1.0");

	}

	

}
