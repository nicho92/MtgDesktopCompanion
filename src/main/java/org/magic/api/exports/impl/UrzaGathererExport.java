package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.tools.FileTools;
import org.magic.tools.UITools;

public class UrzaGathererExport extends AbstractFormattedFileCardExport {
	
	private static final String COLUMNS="Name,Type,Color,Rarity,Author,Power,Toughness,Mana cost,Converted mana cost,Count,Foil count,Special foil count,Price,Foil price,Number,Set,ID,Multiverse ID,Comments,To trade,Condition,Grading,Languages,TCG ID,Cardmarket ID";
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder("\"sep=").append(getSeparator()).append("\"").append(System.lineSeparator());
		  			  temp.append(COLUMNS).append(",Deck count,Sideboard count,Maybeboard count").append(System.lineSeparator());
		
		  
		  			  
	}

	@Override
	public MagicDeck importDeck(String content, String name) throws IOException {
		var d = new MagicDeck();
		d.setName(name);
		
		for(MagicCardStock st : importStock(content))
			d.getMain().put(st.getProduct(), st.getQte());
	
		return d;
	}

	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		StringBuilder temp = new StringBuilder("\"sep=").append(getSeparator()).append("\"").append(System.lineSeparator());
					  temp.append(COLUMNS).append(System.lineSeparator());
					  
					  for(var mcs : stock)
						{
							temp.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator());
							temp.append("\"").append(mcs.getProduct().getFullType()).append("\"").append(getSeparator());
							temp.append(parseColors(mcs.getProduct())).append(getSeparator());
							temp.append(mcs.getProduct().getRarity().toPrettyString()).append(getSeparator());
							temp.append("\"").append(mcs.getProduct().getArtist()).append("\"").append(getSeparator());
							temp.append(mcs.getProduct().getPower()).append(getSeparator());
							temp.append(mcs.getProduct().getToughness()).append(getSeparator());
							temp.append(mcs.getProduct().getCost()).append(getSeparator());
							temp.append(mcs.getProduct().getCmc()).append(getSeparator());
							
							temp.append(!mcs.isFoil()?mcs.getQte():0).append(getSeparator());
							temp.append(mcs.isFoil()?mcs.getQte():0).append(getSeparator());
							temp.append(mcs.isEtched()?mcs.getQte():0).append(getSeparator());
							
							temp.append("$").append(!mcs.isFoil()?UITools.formatDouble(mcs.getPrice()).replace(",", "."):0).append(getSeparator());
							temp.append("$").append(mcs.isFoil()?UITools.formatDouble(mcs.getPrice()).replace(",", "."):0).append(getSeparator());
							
							temp.append(mcs.getProduct().getCurrentSet().getNumber()).append(getSeparator());
							temp.append("\"").append(mcs.getProduct().getCurrentSet().getSet()).append("\"").append(getSeparator());
							temp.append("-1").append(getSeparator());
							temp.append(mcs.getProduct().getCurrentSet().getMultiverseid()).append(getSeparator());
							temp.append("\"").append(mcs.getComment()).append("\"").append(getSeparator());
							temp.append("0").append(getSeparator());
							temp.append("\"").append(mcs.getQte()).append("x").append(PluginsAliasesProvider.inst().getConditionFor(this, mcs.getCondition())).append("\"").append(getSeparator());
							temp.append("\"").append(mcs.getGrade()).append("\"").append(getSeparator());
							temp.append("\"").append(mcs.getLanguage()).append("\"").append(getSeparator());
							temp.append(mcs.getProduct().getTcgPlayerId()).append(getSeparator());
							temp.append(mcs.getProduct().getMkmId()).append(getSeparator());
							temp.append(System.lineSeparator());
							notify(mcs.getProduct());
						}
		
		FileTools.saveFile(f, temp.toString());
		
		
	}
	
	private String parseColors(MagicCard mc) {
		
		
		if(mc.isMultiColor())
			return "Multi-couleurs";
		
		if(mc.isColorless())
			return "Sans couleur";
		
		
		switch(mc.getColors().get(0))
		{
			case WHITE: return "Blanc";
			case BLUE: return "Bleu";
			case BLACK: return "Noir";
			case RED : return "Rouge";
			case GREEN: return "Vert";
			default : return "";
		}
		
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		// TODO Auto-generated method stub
		return super.importStock(content);
	}
	
	
	
	
	@Override
	public String getName() {
	return "UrzaGatherer";
	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"sep=,","Name"};
	}

	@Override
	protected String getStringPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

	

}
