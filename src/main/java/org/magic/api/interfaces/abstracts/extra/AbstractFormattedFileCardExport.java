package org.magic.api.interfaces.abstracts.extra;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Strings;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.tools.UITools;

public abstract class AbstractFormattedFileCardExport extends AbstractCardExport {

	public boolean isFile()
	{
		return true;
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.FILE;
	}

	protected abstract boolean skipFirstLine();

	protected abstract String[] skipLinesStartWith();

	protected String getStringPattern()
	{
		return aliases.getRegexFor(this, "default");
	}

	protected abstract String getSeparator();

	protected enum FORMAT_SEARCH { ID, NAME,NUMBER}


	

	protected String[] splitLines(String content,boolean removeBlank)
	{
		String[] arr = UITools.stringLineSplit(content,removeBlank);

		if(skipFirstLine())
			arr = ArrayUtils.remove(arr,0);

		return arr;
	}


	protected MTGCard parseMatcherWithGroup(Matcher m,int gCard,int gEdition,boolean cleaning,FORMAT_SEARCH setSearch, FORMAT_SEARCH cardSearch)
	{
		MTGEdition ed = null;
		if(gEdition > -1)
		{
		
			try {
				if(setSearch==FORMAT_SEARCH.ID)
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(aliases.getSetIdFor(this,m.group(gEdition)));
				else
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(aliases.getSetNameFor(this,m.group(gEdition)));
			
			} catch (Exception _) {
				ed = null;
			}
		}
		String cname = m.group(gCard);

		if(cleaning)
			cname = cleanName(cname);
		
		try {

			switch(cardSearch)
			{
			 case ID:return getEnabledPlugin(MTGCardsProvider.class).getCardById(cname);
			 case NAME:return getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname,ed,true).get(0);
			 case NUMBER : return getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(cname, ed);
			 default : return null;
			}
			

		} catch (Exception e) {
			logger.error("Couldn't find card {} [{}] : {}",cname,ed,e);
			return null;
		}
	}

	public List<Matcher> matches(String content,boolean removeBlank)
	{
		return matches(content, removeBlank, getStringPattern());
	}

	
	
	public List<Matcher> matches(String content,boolean removeBlank, String pattern)
	{
		
		logger.info("Reading deck with regex {}", pattern);
		
		var ret = new ArrayList<Matcher>();
		var p = Pattern.compile(pattern);
		for(var line : splitLines(content,removeBlank))
		{
			line = line.trim();
			if (!Strings.CS.startsWithAny(line, skipLinesStartWith())) {
				var m = p.matcher(line);
				if(m.find())
				{
					logger.debug("Found {}", line);
					ret.add(m);
				}
				else
					logger.error("no match for {}",line);
			}

		}
		return ret;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("SEPARATOR", new MTGProperty(",","Item separator used for export"));
		return m;
	}

}
