package org.magic.api.interfaces.abstracts.extra;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;
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


	
	protected String commated(String name)
	{
		if(name.indexOf(',')>-1)
			return "\""+name+"\"";
		
		return name;
	}


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


	public List<Matcher> matches(File f,boolean removeBlank, Charset charset) throws IOException
	{
		return matches(FileTools.readFile(f, charset),removeBlank);
	}

	public List<Matcher> matches(File f,boolean removeBlank) throws IOException
	{
		return matches(f, removeBlank,MTGConstants.DEFAULT_ENCODING);
	}

	public List<Matcher> matches(String content,boolean removeBlank)
	{
		return matches(content, removeBlank, getStringPattern());
	}

	
	
	public List<Matcher> matches(String content,boolean removeBlank, String pattern)
	{
		
		logger.info("Reading deck with regex {}", pattern);
		
		List<Matcher> ret = new ArrayList<>();
		var p = Pattern.compile(pattern);
		for(String line : splitLines(content,removeBlank))
		{
			line = line.trim();
			if (!StringUtils.startsWithAny(line, skipLinesStartWith())) {
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

	@Override
	public String getVersion() {
		return "2.0";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

}
