package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.m;
import org.hsqldb.lib.FileUtil;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class Apprentice2DeckExport extends AbstractFormattedFileCardExport {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public String getName() {
		return "Apprentice";
	}

	@Override
	public String getFileExtension() {
		return ".dec";
	}

	@Override
	public boolean skipFirstLine() {
		return false;
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append("MD,");
			temp.append(deck.getMap().get(mc) + getSeparator());
			temp.append("\"" + mc.getName() + "\",");
			temp.append(mc.getCurrentSet().getId());
			temp.append("\n");
			notify(mc);
		}
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("SB,");
			temp.append(deck.getMapSideBoard().get(mc) + getSeparator());
			temp.append("\"" + mc.getName() + "\",");
			temp.append(mc.getCurrentSet().getId());
			temp.append("\n");
			notify(mc);
		}
		
		FileUtils.write(dest, temp.toString(),MTGConstants.DEFAULT_ENCODING);
		
	}
	
	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
			MagicDeck deck = new MagicDeck();
			deck.setName(name);
			
			
			for(Matcher m : matches(f))
			{
				MagicEdition ed = null;
				try {
					ed = new MagicEdition(m.group(4));
				} catch (Exception e) {
					ed = null;
					
				}
				String cname = m.group(3).replaceAll("\"", "");
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( cname, ed, true).get(0);
				Integer qte = Integer.parseInt(m.group(2));
				
				if (m.group(1).startsWith("SB"))
					deck.getMapSideBoard().put(mc, qte);
				else
					deck.getMap().put(mc, qte);
				
			}
			return deck;
	}

	
	@Override
	public String getVersion() {
		return "2.0";
	}

	@Override
	public String[] skipLinesStartWith() {
		return new String[] {"//"};
	}

	@Override
	public String getStringPattern() {
		return "(MD|SB)"+getSeparator()+"(\\d+)"+getSeparator()+"(\"[^\"]*\")"+getSeparator()+"{0,1}([^\"]*){0,1}$";
	}

	@Override
	public String getSeparator() {
		return getString("SEPARATOR");
	}

}
