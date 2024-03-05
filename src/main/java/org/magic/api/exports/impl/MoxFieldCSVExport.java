package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class MoxFieldCSVExport extends AbstractFormattedFileCardExport {

	
	String columns = "\"Count\",\"Tradelist Count\",\"Name\",\"Edition\",\"Condition\",\"Language\",\"Foil\",\"Tags\",\"Last Modified\",\"Collector Number\",\"Alter\",\"Proxy\",\"Purchase Price\""; 
			
			
	@Override
	public String getFileExtension() {
		return ".csv"; 
	}

	@Override
	public String getName() {
		return "MoxField";
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.WEBSITE;
	}
	

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		List<MTGCardStock> list = new ArrayList<>();
		matches(content,true).forEach(m->{

			MTGEdition ed = null;

			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(m.group(4));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for {}",m.group(4));
			}

			String cname = cleanName(m.group(3));

			String number=null;
			try {
				number = m.group(10);
			}
			catch(IndexOutOfBoundsException e)
			{
				//do nothing
			}

			MTGCard mc=null;

			if(number!=null && ed !=null)
			{
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);
				} catch (Exception e) {
					logger.error("no card found with number {}/{}",number,ed);
				}
			}

			if(mc==null)
			{
				try {
					mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.ID,FORMAT_SEARCH.NAME);
				} catch (Exception e) {
					logger.error("no card found for {}/{}",cname,ed);
				}
			}

			if(mc!=null) {
				  var mcs = MTGControler.getInstance().getDefaultStock();
					   mcs.setQte(Integer.parseInt(m.group(1)));
					   mcs.setProduct(mc);
					   mcs.setCondition(aliases.getReversedConditionFor(this,m.group(5),null));
					   
					   if(!m.group(6).isEmpty())
						   mcs.setLanguage(m.group(6));

					   mcs.setFoil(m.group(7).equals("foil"));
					   mcs.setSigned(m.group(9).equalsIgnoreCase("true"));
					   mcs.setAltered(m.group(11).equalsIgnoreCase("true"));

					   if(!m.group(13).isEmpty())
						   mcs.setPrice(UITools.parseDouble(m.group(13)));

					   notify(mcs.getProduct());
			   list.add(mcs);
			}
			else
			{
				logger.error("No cards found for {}",cname);
			}


		});
		
		return list;
		
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		var sb = new StringBuilder(columns);
			 sb.append(System.lineSeparator());
			 
			 
			 for(var mcs : stock)
			 {
				 sb.append("\"").append(mcs.getQte()).append("\"").append(getSeparator());
				 sb.append("\"1\"").append(getSeparator());
				 sb.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator());
				 sb.append("\"").append(mcs.getProduct().getEdition().getId()).append("\"").append(getSeparator());
				 sb.append("\"").append(aliases.getConditionFor(this, mcs.getCondition())).append("\"").append(getSeparator());
				 sb.append("\"").append(mcs.getLanguage()).append("\"").append(getSeparator());
				 sb.append("\"").append(mcs.isFoil()?"foil":"").append("\"").append(getSeparator());
				 sb.append("\"").append("").append("\"").append(getSeparator());
				 sb.append("\"").append(UITools.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")).append("\"").append(getSeparator());
				 sb.append("\"").append(mcs.getProduct().getNumber()).append("\"").append(getSeparator());
				 
				 sb.append("\"").append(mcs.isAltered()?"True":"False").append("\"").append(getSeparator());
				 sb.append("\"").append(mcs.getCondition().equals(EnumCondition.PROXY)?"True":"False").append("\"").append(getSeparator());
				 sb.append("\"").append(mcs.getPrice()).append("\"");
				 sb.append(System.lineSeparator());
				 
				 notify(mcs.getProduct());
			 }
			 FileTools.saveFile(f, sb.toString());
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
		return ",";
	}

}
