package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;

public class MTGStandExport extends AbstractFormattedFileCardExport {

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);

	}
	

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(name);
		
		for(MagicCardStock st : importStock(f))
		{
			d.getMain().put(st.getMagicCard(), st.getQte());
		}
		return d;
	}
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		// TODO Auto-generated method stub
		super.exportStock(stock, f);
	}
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		List<MagicCardStock> ret = new ArrayList<>();
		for(Matcher m : matches(content, true))
		{
			MagicCard mc = parseMatcherWithGroup(m, 1, 4, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
			
			if(mc !=null )
			{
				
				MagicCardStock st = new MagicCardStock();
				st.setMagicCard(mc);
				st.setQte(Integer.parseInt(m.group(2)));
				st.setLanguage(m.group(6));
				st.setFoil(m.group(7).equals("1"));
				st.setComment(m.group(10));
				
				ret.add(st);
				notify(mc);
			}
			
			
			
		}
		
		return ret;
	}
	

	@Override
	public String getName() {
		return "MTGStand";
	}

	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {" "};
	}

	@Override
	protected String getStringPattern() {
		return "\"(.*?)\",(\\d+),(.*?),(.*?),(\\d+),(.*?),(\\d+),\"(.*?)\",(.*?),(.*?)$";
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

}
