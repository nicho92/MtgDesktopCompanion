package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class CardSphereExport extends AbstractFormattedFileCardExport {

	private final String firstColumn="Count,Tradelist Count,Name,Edition,Condition,Language,Foil,Tags";

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		// TODO Auto-generated method stub
		return super.importStock(content);
	}
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		var buff = new StringBuilder(firstColumn).append(System.lineSeparator());
		
		stock.forEach(mcs->
			
			buff
			.append("\"").append(mcs.getQte()).append("\"").append(getSeparator())
			.append("\"").append(mcs.getQte()).append("\"").append(getSeparator())
			.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator())
			.append("\"").append(mcs.getProduct().getCurrentSet().getSet()).append("\"").append(getSeparator())
			.append("\"").append(aliases.getConditionFor(this, mcs.getCondition())).append("\"").append(getSeparator())
			.append("\"").append(mcs.getLanguage()).append("\"").append(getSeparator())
			.append("\"").append(mcs.isFoil()?"Foil":"").append("\"").append(getSeparator())
			.append("\"").append("").append("\"").append(System.lineSeparator())
		);
		
		FileTools.saveFile(f, buff.toString());
		
	}
	
	
	
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
			d.getMain().put(st.getProduct(), st.getQte());

		return d;
	}

	@Override
	public String getName() {
		return "CardSphere";
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
