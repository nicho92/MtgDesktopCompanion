package org.magic.api.exports.impl;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.CardKingdomTools;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;

public class CardKingdomCSVExport extends AbstractFormattedFileCardExport {


	
	private String columns="Name,Edition,Foil,Qty\n";

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File dest) throws IOException {
		var line = new StringBuilder(columns);
		for(MTGCardStock mc : stock)
		{
			String name= CardKingdomTools.getCKFormattedName(mc.getProduct());
			String set = CardKingdomTools.getCKFormattedSet(mc.getProduct());

			line.append(commated(name)).append(getSeparator());
			line.append(set).append(getSeparator());
			line.append(String.valueOf(mc.isFoil())).append(getSeparator());
			line.append(mc.getQte()).append(System.lineSeparator());
			notify(mc.getProduct());
		}
		FileTools.saveFile(dest, line.toString());
	}



	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{

			MTGEdition ed = null;

			try {
				ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName( aliases.getReversedSetNameFor(new CardKingdomCardExport() , m.group(4)));
			}
			catch(Exception _)
			{
				logger.error("Edition not found for {}",m.group(4));
			}

			String cname = cleanName(m.group(3));

			String number=null;
			try {
				number = m.group(5);
			}
			catch(IndexOutOfBoundsException _)
			{
				//do nothing
			}

			MTGCard mc=null;

			if(number!=null && ed !=null)
			{
				try {
					mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);
				} catch (Exception _) {
					logger.error("no card found with number {}/{}",number,ed);
				}
			}

			if(mc==null)
			{
				try {
					mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.NAME);
				} catch (Exception _) {
					logger.error("no card found for {} / {} ",cname,ed);
				}
			}

			if(mc!=null) {
				MTGCardStock mcs = MTGControler.getInstance().getDefaultStock();
					   mcs.setQte(Integer.parseInt(m.group(1)));
					   mcs.setProduct(mc);
					   mcs.setCondition(aliases.getReversedConditionFor(new CardKingdomCardExport(),m.group(6),null));

					   if(!m.group(7).isEmpty())
						   mcs.setLanguage(m.group(7));

					   mcs.setFoil(m.group(8)!=null);
					   mcs.setSigned(m.group(9)!=null);
					   mcs.setAltered(m.group(11)!=null);

					   if(!m.group(15).isEmpty())
						   mcs.setPrice(Double.parseDouble(m.group(15)));

			   list.add(mcs);
			}
			else
			{
				logger.error("No cards found for {}" ,cname);
			}


		});

		return list;
	}

	@Override
	public String getName() {
		return "Card Kingdom CSV";
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
	public Icon getIcon() {
		return new ImageIcon(new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/card kingdom.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH));
	}




	@Override
	public String getSeparator() {
		return ",";
	}

}