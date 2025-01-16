package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class UrzaGathererExport extends AbstractFormattedFileCardExport {

	private static final String COLUMNS="Name,Type,Color,Rarity,Author,Power,Toughness,Mana cost,Converted mana cost,Count,Foil count,Special foil count,Price,Foil price,Number,Set,ID,Multiverse ID,Comments,To trade,Condition,Grading,Languages,TCG ID,Cardmarket ID";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}


	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder("\"sep=").append(getSeparator()).append("\"").append(System.lineSeparator());
		  			  temp.append(COLUMNS).append(",Deck count,Sideboard count,Maybeboard count").append(System.lineSeparator());

		writeDeckLine(temp,deck.getMain().entrySet(),1);
		writeDeckLine(temp,deck.getSideBoard().entrySet(),2);
	
		FileTools.saveFile(dest, temp.toString());
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		List<MTGCardStock> list = new ArrayList<>();

		matches(content, true).forEach(m->{

		MTGCard mc=readCard(m);
		if(mc!=null)
		{

			int nbFoil = Integer.parseInt(m.group(11));
			int nbNormal = Integer.parseInt(m.group(10));
			int nbEtched = Integer.parseInt(m.group(12));

			var st = buildStockItem(mc,m,nbNormal,false);
			list.add(st);


			if(nbFoil>0)
				list.add(buildStockItem(mc,m,nbFoil,true));


			if(nbEtched>0)
			{
				var st3 = buildStockItem(mc,m,nbEtched,false);
				st3.setQte(nbEtched);
				list.add(st3);
			}
			notify(mc);
		}
		});

		return list;
	}

	private MTGCardStock buildStockItem(MTGCard mc , Matcher m,Integer qty,boolean foil)
	{

		var st = MTGControler.getInstance().getDefaultStock();
		st.setProduct(mc);
		st.setLanguage(m.group(23));
		st.setFoil(foil);
		st.setComment(m.group(19));
		if(foil)
			st.setPrice(UITools.parseDouble(m.group(14).trim()));
		else
			st.setPrice(UITools.parseDouble(m.group(13).trim()));
		st.setQte(qty);
		var strCondition = m.group(21);
		if(strCondition.indexOf("x")>-1)
			strCondition = strCondition.substring(strCondition.indexOf("x")+1);

		st.setCondition(aliases.getReversedConditionFor(this, strCondition, EnumCondition.NEAR_MINT)  );


		return st;
	}


	private MTGCard readCard(Matcher m) {
		try {
			return getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(1),null,true).stream().filter(c->
				(!m.group(18).isEmpty()&&m.group(18).equals(c.getMultiverseid()))||(m.group(15).equals(c.getNumber()))
			).findFirst().orElse(null);
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}


	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		StringBuilder temp = new StringBuilder("\"sep=").append(getSeparator()).append("\"").append(System.lineSeparator());
					  temp.append(COLUMNS).append(System.lineSeparator());

					  for(var mcs : stock)
						{
							writeLine(temp,mcs);
							temp.append(System.lineSeparator());

						}

		FileTools.saveFile(f, temp.toString());


	}


	private void writeDeckLine(StringBuilder temp, Set<Entry<MTGCard, Integer>> set, int i )
	{

		set.forEach(entry->{
			var mcs= MTGControler.getInstance().getDefaultStock();
			mcs.setProduct(entry.getKey());
			mcs.setQte(entry.getValue());

			writeLine(temp, mcs);

			temp.append(getSeparator()).append((i==1)?entry.getValue():0)
			.append(getSeparator()).append((i==2)?entry.getValue():0)
			.append(getSeparator()).append((i==3)?entry.getValue():0)
			.append(System.lineSeparator());
		});


	}


	private void writeLine(StringBuilder temp,MTGCardStock mcs) {
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

		temp.append("$").append(!mcs.isFoil()?UITools.formatDouble(mcs.getValue().doubleValue()).replace(",", "."):0).append(getSeparator());
		temp.append("$").append(mcs.isFoil()?UITools.formatDouble(mcs.getValue().doubleValue()).replace(",", "."):0).append(getSeparator());

		temp.append(mcs.getProduct().getNumber()).append(getSeparator());
		temp.append("\"").append(mcs.getProduct().getEdition().getSet()).append("\"").append(getSeparator());
		temp.append("-1").append(getSeparator());
		temp.append(mcs.getProduct().getMultiverseid()).append(getSeparator());
		temp.append("\"").append(mcs.getComment()).append("\"").append(getSeparator());
		temp.append("0").append(getSeparator());
		temp.append("\"").append(mcs.getQte()).append("x").append(aliases.getConditionFor(this, mcs.getCondition())).append("\"").append(getSeparator());
		temp.append("\"").append(mcs.getGrade()).append("\"").append(getSeparator());
		temp.append("\"").append(mcs.getLanguage()).append("\"").append(getSeparator());
		temp.append(mcs.getProduct().getTcgPlayerId()).append(getSeparator());
		temp.append(mcs.getProduct().getMkmId());
		notify(mcs.getProduct());
	}


	private String parseColors(MTGCard mc) {


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
	public String getName() {
	return "UrzaGatherer";
	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"\"sep=,","Name"};
	}


	@Override
	protected String getSeparator() {
		return ",";
	}



}
