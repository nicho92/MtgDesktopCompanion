package org.magic.api.interfaces.abstracts;

import java.util.regex.Pattern;

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
	
	
	public abstract String[] skipLinesStartWith();

	public abstract String getStringPattern();
	
	public abstract String getSeparator();
	
	public String[] splitLines(String content)
	{
		return UITools.stringLineSplit(content);
	}
	
	
	
	public Pattern getPattern()
	{
		return Pattern.compile(getStringPattern());
	}
	
	
	@Override
	public void initDefault() {
		setProperty("SEPARATOR", ",");
	}
}
