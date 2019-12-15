package org.beta;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;

public class DelverLensExport extends AbstractFormattedFileCardExport{

	@Override
	public String getFileExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean skipFirstLine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getStringPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSeparator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void initDefault() {
		setProperty("SEPARATOR", ",");
	}
	

}
