package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.tools.FileTools;
import org.magic.tools.MTG;

public class MagicAlbumExport extends AbstractFormattedFileCardExport {

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);

	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		var d = new MagicDeck();
		d.setName(name);
		
		for(MagicCardStock st : importStock(f))
		{
			d.getMain().put(st.getProduct(), st.getQte());
		}
		return d;
	}
	
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileTools.readFile(f,StandardCharsets.UTF_16));
	}
	
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		StringBuilder temp = new StringBuilder();
		var endOfLine="\r\n";
		temp.append("Name (Oracle)	Name	Set	Language	Qty (R)	Qty (F)	Proxies	Buy Qty	Number	Cost	Rarity	Legality");
		temp.append(endOfLine);
		
		
		for(var mcs : stock)
		{
			temp.append(PluginsAliasesProvider.inst().getReversedSetIdFor(this, mcs.getProduct().getCurrentSet().getId())).append("\t");
			temp.append(mcs.getProduct().getName()).append("\t");
			temp.append(mcs.getProduct().getForeignNames().get(0).getName()).append("\t");
			temp.append("").append("\t");
			temp.append(mcs.getLanguage()).append("\t");
			temp.append(mcs.isFoil()?"":mcs.getQte()).append("\t");
			temp.append(mcs.isFoil()?mcs.getQte():"").append("\t");
			temp.append(mcs.getComment()==null?"":mcs.getComment()).append("\t");
			temp.append(mcs.getProduct().getRarity().toPrettyString().charAt(0)).append("\t");
			temp.append(mcs.getProduct().getCurrentSet().getNumber()).append("/").append(mcs.getProduct().getCurrentSet().getCardCountOfficial()).append("\t");
			temp.append(mcs.getProduct().getColors()).append("\t");
			temp.append(mcs.getProduct().getCost()).append("\t");
			temp.append(mcs.getProduct().isCreature()?mcs.getProduct().getPower()+"/"+mcs.getProduct().getToughness():"").append("\t");
			temp.append(mcs.getProduct().getArtist()).append("\t");
			temp.append(mcs.getProduct().getBorder()!=null?mcs.getProduct().getBorder().toPrettyString():"").append("\t");
			temp.append("™ & © "+Calendar.getInstance().get(Calendar.YEAR)+" Wizards of the Coast").append("\t");
			temp.append(mcs.getProduct().getFullType()).append("\t");
			
			
			
			
			
			temp.append(endOfLine);
			notify(mcs.getProduct());
		}
		
		
		FileTools.saveFile(f, temp.toString(),StandardCharsets.UTF_16);
		
		
	}
	
	public static void main(String[] args) {
		File f = new File("G:\\Mon Drive\\magicalbum.csv");
	}
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		var ret = new ArrayList<MagicCardStock>();
		matches(content, true ).forEach(m->{
			
			var foilnumber = ( !m.group(7).isEmpty()) ? Integer.parseInt(m.group(7)):0;
			var regularNumber = ( !m.group(6).isEmpty()) ? Integer.parseInt(m.group(6)):0;
			var setCode = PluginsAliasesProvider.inst().getSetIdFor(this, new MagicEdition(m.group(1)));
			var lang=m.group(5);
			var cardName = m.group(2).replace("’", "'").replace("│", " // ");
			
			try {
				MagicCard mc=null;
				
				if(!m.group(10).isEmpty())
				{
					var cardNumber=m.group(10).split("/")[0].replaceFirst("^0+(?!$)", "");
					mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(cardNumber, setCode);
				}
				else
				{
					var listmc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(setCode),true);
					
					if(listmc.isEmpty())
					{
						logger.error(cardName + " is not found in set "+setCode);
						mc=null;
					}
					else
					{
						mc=listmc.get(0);
					}
				}
				
				if(mc!=null)
				{
					var mcs = MTGControler.getInstance().getDefaultStock();
					mcs.setProduct(mc);
					mcs.setQte(regularNumber);
					mcs.setLanguage(lang);
					ret.add(mcs);
				
				
				if(foilnumber>0)
				{
					var mcsF = MTGControler.getInstance().getDefaultStock();
					mcsF.setProduct(mc);
					mcsF.setQte(regularNumber);
					mcsF.setLanguage(lang);
					mcsF.setFoil(true);
					ret.add(mcsF);
				}
				
				notify(mc);
				}
			} catch (IOException e) {
				logger.error("error getting " + cardName);
			}
			
			
		});
		
		
		return ret;
	}
	

	@Override
	public String getName() {
		return "Magic Album";
	}

	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[0];
	}

	@Override
	protected String getStringPattern() {
		return "(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)";
	}

	@Override
	protected String getSeparator() {
		return "\t";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("SEPARATOR",getSeparator());
	}
	
	
}
