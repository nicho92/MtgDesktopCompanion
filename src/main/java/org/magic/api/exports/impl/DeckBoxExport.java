package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class DeckBoxExport extends AbstractCardExport {

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public String getFileExtension() {
		return ".deckbox";
	}

	private String translate(EnumCondition condition)
	{
		switch (condition)
		{
		 case LIGHTLY_PLAYED : return "Good (Lightly Played)";
		 case NEAR_MINT : return "Near Mint";
		 case PROXY : return "";
		 default : return condition.name(); 
		}
	}
	
	private EnumCondition reverse(String condition)
	{
		switch (condition)
		{
		 case "Good (Lightly Played)": return EnumCondition.LIGHTLY_PLAYED;
		 case "Near Mint":  return EnumCondition.NEAR_MINT; 
		 default : EnumCondition.valueOf(condition.toUpperCase());
		}
		return EnumCondition.valueOf(condition.toUpperCase());
	}
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File dest) throws IOException {
		String columns="Count,Tradelist Count,Name,Edition,Card Number,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint,Promo,Textless,My Price\n";
		FileUtils.write(dest, columns, MTGConstants.DEFAULT_ENCODING,false);
		for(MagicCardStock mc : stock)
		{
			String name=mc.getMagicCard().getName();
			if(mc.getMagicCard().getName().contains(","))
				name="\""+mc.getMagicCard().getName()+"\"";
			
			StringBuilder line = new StringBuilder();
			line.append(mc.getQte()).append(",");
			line.append(mc.getQte()).append(",");
			line.append(name).append(",");
			line.append(mc.getMagicCard().getCurrentSet().getSet()).append(",");
			line.append(mc.getMagicCard().getCurrentSet().getNumber()).append(",");
			line.append(translate(mc.getCondition())).append(",");
			line.append(mc.getLanguage()).append(",");
			line.append(mc.isFoil()?"foil":"").append(",");
			line.append(mc.isSigned()?"signed":"").append(",");
			line.append(",");
			line.append(mc.isAltered()?"altered":"").append(",");
			line.append(",");
			line.append(",");
			line.append(",");
			line.append(mc.getPrice()).append("\n");
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			notify(mc);
		}
	}
	
	
	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		
		String columns="Count,Tradelist Count,Name,Edition,Card Number,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint,Promo,Textless,My Price\n";
		FileUtils.write(dest, columns, MTGConstants.DEFAULT_ENCODING,false);
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains(","))
				name="\""+mc.getName()+"\"";
			
			StringBuilder line = new StringBuilder();
			line.append(deck.getMap().get(mc)).append(",");
			line.append(deck.getMap().get(mc)).append(",");
			line.append(name).append(",");
			line.append(mc.getCurrentSet().getSet()).append(",");
			line.append(mc.getCurrentSet().getNumber()).append(",");
			line.append("Near Mint,,,,,,,,,0\n");
			
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			notify(mc);
		}

	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		
		List<MagicCardStock> list = new ArrayList<>();
		try (BufferedReader read = new BufferedReader(new FileReader(f))) {
			String line = read.readLine();
			while (line != null) {
				line = read.readLine();
				if(line==null || line.isEmpty())
					break;
				
				MagicCardStock mcs = new MagicCardStock();
				
				mcs.setQte(Integer.parseInt(line.substring(0, line.indexOf(','))));
				
				line=line.substring(line.indexOf(',')+1,line.length());
				line=line.substring(line.indexOf(',')+1,line.length()); //don't care the next one
				Pattern p = Pattern.compile("\"([^\"]*)\"");
				Matcher m = p.matcher(line);
				String name=null;
				if(m.find())
				{
					name=m.group(1);
					line=line.substring(line.lastIndexOf('"')+2,line.length());
				}
				else
				{
					name=line.substring(0, line.indexOf(','));
					line=line.substring(line.indexOf(',')+1,line.length());
				}
				MagicEdition ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(line.substring(0, line.indexOf(',')));
				line=line.substring(line.indexOf(',')+1,line.length());
				line=line.substring(line.indexOf(',')+1,line.length()); //don't care of number
				
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(name, ed, true).get(0);
				mcs.setMagicCard(mc);
				
		
				if(!line.startsWith(","))
				{
					String condition = line.substring(0, line.indexOf(','));
					mcs.setCondition(reverse(condition));
				}
				line=line.substring(line.indexOf(',')+1,line.length());
				
				mcs.setLanguage(line.substring(line.indexOf(',')+1,line.length()));
				line=line.substring(line.indexOf(',')+1,line.length());
				
				mcs.setAltered(line.contains("Altered Art"));
				mcs.setFoil(line.contains("Foiled"));
				mcs.setSigned(line.contains("Signed"));
				
				
				list.add(mcs);
			}
		}
		
		return list;
	}
	
	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		
		File file = new File(name);
		FileUtils.write(file, f,MTGConstants.DEFAULT_ENCODING);
		return importDeck(file);
	}
	
	

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		List<MagicCardStock> list = importStock(f);
		MagicDeck d = new MagicDeck();
		
		for(MagicCardStock st : list)
		{
			d.getMap().put(st.getMagicCard(), st.getQte());
		}
		
		return d;
		
	}

	@Override
	public String getName() {
		return "DeckBox";
	}

}
