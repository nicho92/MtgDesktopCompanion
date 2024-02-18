package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;

public class MagicAlbumExport extends AbstractFormattedFileCardExport {

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}


	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileTools.readFile(f,StandardCharsets.UTF_16));
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		StringBuilder temp = new StringBuilder();
		var endOfLine="\r\n";
		temp.append("Set\tName (Oracle)\tName\tVersion\tLanguage\tQty (R)\tQty (F)\tBuy Qty\tProxies\tNotes\tRarity\tNumber\tColor\tCost\tP/T\tArtist\tBorder\tCopyright\tType\tSell Qty\tGrade (R)\tGrade (F)\tPrice (R)\tPrice (F)\tUsed\tType (Oracle)\tLegality\tBuy Price\tSell Price\tRating\tObject\r\n");
		temp.append(endOfLine);


		for(var mcs : stock)
		{
			temp.append(aliases.getReversedSetIdFor(this, mcs.getProduct().getCurrentSet().getId())).append("\t");
			temp.append(mcs.getProduct().getName()).append("\t");
			temp.append(mcs.getProduct().getForeignNames().get(0).getName()).append("\t");
			temp.append("").append("\t");
			temp.append(mcs.getLanguage()).append("\t");
			temp.append(mcs.isFoil()?"":mcs.getQte()).append("\t");
			temp.append(mcs.isFoil()?mcs.getQte():"").append("\t");
			temp.append(mcs.getComment()==null?"":mcs.getComment()).append("\t");
			temp.append(mcs.getProduct().getRarity().toPrettyString().charAt(0)).append("\t");
			temp.append(mcs.getProduct().getNumber()).append("/").append(mcs.getProduct().getCurrentSet().getCardCountOfficial()).append("\t");
			temp.append(mcs.getProduct().getColors()).append("\t");
			temp.append(mcs.getProduct().getCost()).append("\t");
			temp.append(mcs.getProduct().isCreature()?mcs.getProduct().getPower()+"/"+mcs.getProduct().getToughness():"").append("\t");
			temp.append(mcs.getProduct().getArtist()).append("\t");
			temp.append(mcs.getProduct().getBorder()!=null?mcs.getProduct().getBorder().toPrettyString():"").append("\t");
			temp.append("™ & © "+mcs.getProduct().getCurrentSet().getReleaseDate()+" Wizards of the Coast").append("\t");
			temp.append(mcs.getProduct().getFullType()).append("\t");





			temp.append(endOfLine);
			notify(mcs.getProduct());
		}


		FileTools.saveFile(f, temp.toString(),StandardCharsets.UTF_16);


	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var ret = new ArrayList<MTGCardStock>();
		matches(content, true ).forEach(m->{

			var foilnumber = ( !m.group(7).isEmpty()) ? Integer.parseInt(m.group(7)):0;
			var regularNumber = ( !m.group(6).isEmpty()) ? Integer.parseInt(m.group(6)):0;
			var proxyNumber = ( !m.group(9).isEmpty()) ? Integer.parseInt(m.group(9)):0;
			var setCode = aliases.getSetIdFor(this, new MTGEdition(m.group(1)));
			var lang=m.group(5);
			var cardName = m.group(2).replace("’", "'").replace("│", " // ");



				MTGCard mc=null;

				if(!m.group(12).isEmpty())
				{
					try{
						var cardNumber=m.group(12).split("/")[0].replaceFirst("^0+(?!$)", "");
						mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(cardNumber, setCode);
					}
					catch(Exception e)
					{
						mc=null;
					}

				}


				if(mc==null)
				{
					try {
						var listmc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(setCode),true);

						if(listmc.isEmpty())
						{
							logger.error("{} is not found in set {}",cardName,setCode);
							mc=null;
						}
						else
						{
							mc=listmc.get(0);
						}
					} catch (IOException e) {
						mc=null;
						logger.error(e);
					}

				}

				if(mc!=null)
				{

				if(regularNumber>0) {
					var mcs = MTGControler.getInstance().getDefaultStock();
					mcs.setProduct(mc);
					mcs.setQte(regularNumber);
					mcs.setLanguage(lang);
					mcs.setFoil(false);
					ret.add(mcs);
				}

				if(foilnumber>0)
				{
					var mcsF = MTGControler.getInstance().getDefaultStock();
					mcsF.setProduct(mc);
					mcsF.setQte(foilnumber);
					mcsF.setLanguage(lang);
					mcsF.setFoil(true);
					ret.add(mcsF);
				}

				if(proxyNumber>0)
				{
					var mcsP = MTGControler.getInstance().getDefaultStock();
					mcsP.setProduct(mc);
					mcsP.setQte(proxyNumber);
					mcsP.setLanguage(lang);
					mcsP.setFoil(false);
					mcsP.setCondition(EnumCondition.PROXY);
					ret.add(mcsP);
				}



				notify(mc);
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
	protected String getSeparator() {
		return "\t";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("SEPARATOR",getSeparator());
	}


}
