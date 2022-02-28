package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.tools.FileTools;

public class MagicAlbum extends AbstractFormattedFileCardExport {

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
		var d = new MagicDeck();
		d.setName(name);
		
		for(MagicCardStock st : importStock(f))
		{
			d.getMain().put(st.getProduct(), st.getQte());
		}
		return d;
	}
	
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileTools.readFile(f,StandardCharsets.UTF_16));
	}
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		var ret = new ArrayList<MagicCardStock>();
		//Set	Name (Oracle)	Name	Version	Language	Qty (R)	Qty (F)	Notes	Rarity	Number	Color	Cost	P/T	Artist	Border	Copyright	Type	Buy Qty	Sell Qty	Buy Price	Sell Price	Grade (R)	Grade (F)	Price (R)	Price (F)	Proxies	Used	Type (Oracle)	Legality	Rating	Object

		matches(content, true ).forEach(m->{
			
			var foilnumber = ( !m.group(7).isEmpty()) ? Integer.parseInt(m.group(7)):0;
			var regularNumber =Integer.parseInt(m.group(6)); 
			var setCode = m.group(1);
			var lang=m.group(5);
			var cardNumber =m.group(10).split("/")[0].replaceFirst("^0+(?!$)", "");
			
			
			
			
			System.out.println(setCode + " foil="+foilnumber + " regular=" +regularNumber + " lang="+lang + " number="+cardNumber);
			
			
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
	protected String getStringPattern() {
		return "(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)";
	}

	@Override
	protected String getSeparator() {
		return "\t";
	}

	public static void main(String[] args) throws IOException {
		File f = new File("G:\\Mon Drive\\inventory.csv");
		new MagicAlbum().importStockFromFile(f);
	}

}
