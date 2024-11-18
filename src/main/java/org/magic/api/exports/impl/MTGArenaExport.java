package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.MTG;

public class MTGArenaExport extends AbstractFormattedFileCardExport {

	boolean side=false;

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	
	
	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {

		var temp = new StringBuilder();

		for(Map.Entry<MTGCard, Integer> entry : deck.getMain().entrySet())
		{
			temp.append(entry.getValue())
				.append(" ")
				.append(entry.getKey())
				.append(" (")
				.append(aliases.getReversedSetIdFor(this,entry.getKey().getEdition()).toUpperCase())
				.append(")")
				.append(" ")
				.append(entry.getKey().getNumber())
				.append("\r\n");
			notify(entry.getKey());

		}

		if(!deck.getSideBoard().isEmpty())
			for(Map.Entry<MTGCard, Integer> entry : deck.getSideBoard().entrySet())
			{
				temp.append("\r\n")
					.append(entry.getValue())
					.append(" ")
					.append(entry.getKey())
					.append(" (")
					.append(aliases.getReversedSetIdFor(this,entry.getKey().getEdition()).toUpperCase())
					.append(")")
					.append(" ")
					.append(entry.getKey().getNumber());
				notify(entry.getKey());
			}

		var selection = new StringSelection(temp.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);

		logger.debug("saved in clipboard");

	}


	@Override
	public MTGDeck importDeck(String f,String dname) throws IOException {
		var deck = new MTGDeck();
		deck.setName(dname);
		side=false;
		var trf = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);

		if(trf==null)
		{
			MTG.notifyError("Clipboard is empty");
			return deck;
		}

		var txt ="";
		try {
			txt =trf.getTransferData(DataFlavor.stringFlavor).toString();

			logger.debug("copy from clipboard ok : {}", txt);
		} catch (UnsupportedFlavorException e) {
			throw new IOException(e);
		}

		matches(txt,false).forEach(m->
		{
			if(StringUtils.isAllEmpty(m.group()))
			{
				side=true;
			}
			else
			{
				try {
					var qte = Integer.parseInt(m.group(1));
					String name = m.group(2).trim();
					String ed =  aliases.getSetIdFor(this,m.group(3));
					MTGEdition me = getEnabledPlugin(MTGCardsProvider.class).getSetById(ed);
					MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( name.trim(), me, true).get(0);
					notify(mc);
					if(!side)
						deck.getMain().put(mc, qte);
					else
						deck.getSideBoard().put(getEnabledPlugin(MTGCardsProvider.class).searchCardByName( name.trim(), me, true).get(0), qte);

				}
				catch(Exception e)
				{
					logger.error("Error loading cards {}", m.group(),e);
				}

			}

		});

		return deck;

	}


	@Override
	public String getName() {
		return "MTGArena";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"//","Deck","Sideboard"};
	}

	@Override
	protected String getSeparator() {
		return " ";
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return new HashMap<>();
	}
	


}
