package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;

public class MTGAssistantCardExport extends AbstractFormattedFileCardExport {

	@Override
	public String getFileExtension() {
		return "csv";
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
	public List<MagicCardStock> importStock(String content) throws IOException {
		List<MagicCardStock> list = new ArrayList<>();
		matches(content,true).forEach(m->{
			
			
				System.out.println(m.group(1));
			
		});
		
		return list;
	}
	
	@Override
	public String getName() {
		return "MTGAssistant";
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
	protected String getStringPattern() {
		return "(.*?);(\\d+);(.*?);(.*?);(\\d+)?;(\\d+)?;(.*?);(.*?);(.*?);(\\d+);(.*?);(true|false);(.*?);";
	}

	@Override
	protected String getSeparator() {
		return ";";
	}

	public static void main(String[] args) throws IOException {
		File f = new File("d:/export2.csv");
		
		new MTGAssistantCardExport().importStockFromFile(f);
		

	}

}
