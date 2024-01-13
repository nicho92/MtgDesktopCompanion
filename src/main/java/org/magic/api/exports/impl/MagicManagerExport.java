package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

import com.google.common.collect.Lists;

public class MagicManagerExport extends AbstractFormattedFileCardExport {

	
	private static final String columns = "Card Name,Set Code,Collector Number,Language,Foil,Count";

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
			d.getMain().put(st.getProduct(), st.getQte());

		return d;
	}
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		// TODO Auto-generated method stub
		return super.importStock(content);
	}
	
	Integer number=0;
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		
		
		
		Lists.partition(stock, getInt("MAX_ITEMS")).forEach(list->{
			var temp = new StringBuilder(columns);
			temp.append(System.lineSeparator());
			
			list.forEach(st->{
				temp.append("\"").append(st.getProduct().getName()).append("\"").append(getSeparator());
				temp.append(aliases.getSetIdFor(this,st.getProduct().getCurrentSet()).toLowerCase()).append(getSeparator());
				temp.append(st.getProduct().getCurrentSet().getNumber()).append(getSeparator());
				temp.append(st.getLanguage().substring(0, 2)).append(getSeparator());
				temp.append(st.isFoil()?"True":"False").append(getSeparator());
				temp.append(st.getQte());
				notify(st.getProduct());
				temp.append(System.lineSeparator());
			});
			
			
			try {
					f.renameTo(new File(f.getAbsolutePath()+"-"+(number++)));
					FileTools.saveFile(f, temp.toString());
			} catch (IOException e) {
					logger.error(e);
			}
		});
		number=0;
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m =  super.getDefaultAttributes();
		
		m.put("MAX_ITEMS", "2000");
		
		return m;
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
	
	@Override
	public String getName() {
		return "MagicManager";
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}


}
