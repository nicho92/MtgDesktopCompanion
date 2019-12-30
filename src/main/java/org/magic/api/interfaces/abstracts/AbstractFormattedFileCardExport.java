package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public abstract class AbstractFormattedFileCardExport extends AbstractCardExport {

	public boolean isFile()
	{
		return true;
	}
	
	@Override
	public boolean needDialogGUI() {
		return false;
	}
	
	@Override
	public boolean needFile() {
		return true;
	}
	
	protected abstract boolean skipFirstLine();
	
	protected abstract String[] skipLinesStartWith();

	protected abstract String getStringPattern();
	
	protected abstract String getSeparator();
	
	
	
	private String[] splitLines(String content,boolean removeBlank)
	{
		String[] arr = UITools.stringLineSplit(content,removeBlank);
		
		if(skipFirstLine())
			arr = ArrayUtils.remove(arr,0);
		return arr;
	}
	
	
	protected MagicCard parseMatcherWithGroup(Matcher m,int gCard,int gEdition,boolean cleaning,boolean setById)
	{
		MagicEdition ed = null;
		try {
			if(setById)
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(m.group(gEdition));
			else
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(gEdition));
		} catch (Exception e) {
			ed = null;
		}
		
		String cname = m.group(gCard);
		
		if(cleaning)
			cname = cleanName(cname);
		
		try {
			return MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( cname, ed, true).get(0);
		} catch (IOException e) {
			logger.error("Couldn't find card "+ cname + " ["+ed+"] :" + e);
			return null;
		}
	}
	
	
	public List<Matcher> matches(File f,boolean removeBlank) throws IOException
	{
		return matches(FileUtils.readFileToString(f, MTGConstants.DEFAULT_ENCODING),removeBlank);
	}
	
	
	public List<Matcher> matches(String content,boolean removeBlank)
	{
		logger.debug("Parsing content with pattern : " + getStringPattern());
		List<Matcher> ret = new ArrayList<>();
		for(String line : splitLines(content,removeBlank)) 
		{
			line = line.trim();
			if (!StringUtils.startsWithAny(line, skipLinesStartWith())) {
				
				Matcher m = getPattern().matcher(line);
				
				if(m.find())
					ret.add(m);
				else
					logger.error("no match for " + line);
			}
			
		}
		return ret;
	}
	
	
	private Pattern getPattern()
	{
		return Pattern.compile(getStringPattern());
	}
	
	
	@Override
	public void initDefault() {
		setProperty("SEPARATOR", ",");
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
