package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public abstract class AbstractCardExport extends AbstractMTGPlugin implements MTGCardsExport {

	@Override
	public MODS getMods() {
		return MODS.BOTH;
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.EXPORT;
	}

	@Override
	public boolean needFile() {
		return true;
	}

	@Override
	public EnumExportCategory getCategory() {
		if(needFile())
			return EnumExportCategory.FILE;

		return EnumExportCategory.NONE;
	}


	protected String commated(String name)
	{
		if(name.indexOf(',')>-1)
			return "\""+name+"\"";
				
		return name;
	}

	protected String cleanName(String cname) {
		
		if(cname==null)
			return "";
		
		cname = cname.replace("\"","").trim();
		if(cname.indexOf('/') > 1)
			cname=cname.substring(0,cname.indexOf('/')).trim();

		if(cname.indexOf('(')>1)
			cname=cname.substring(0,cname.indexOf('(')).trim();



		return cname;
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}

	 @Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}


		@Override
		public MTGDeck importDeck(String f, String name) throws IOException {
			var d = new MTGDeck();
			d.setName(name);

			for(MTGCardStock st : importStock(f))
				d.getMain().put(st.getProduct(), st.getQte());

			return d;
		}



		@Override
		public void exportDeck(MTGDeck deck, File dest) throws IOException {
			exportStock(importFromDeck(deck), dest);
			
		}



	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		var d = new MTGDeck();
		d.setName(FilenameUtils.getBaseName(f.getName()));

		for (MTGCardStock mcs : stock) {
			d.getMain().put(mcs.getProduct(), mcs.getQte());
		}
		exportDeck(d, f);
	}

	protected List<MTGCardStock> importFromDeck(MTGDeck deck) {
		List<MTGCardStock> mcs = new ArrayList<>();

		for (MTGCard mc : deck.getMain().keySet()) {
			MTGCardStock stock = MTGControler.getInstance().getDefaultStock();
			stock.setProduct(mc);
			stock.setQte(deck.getMain().get(mc));
			stock.setComment("import from " + deck.getName());
			stock.setId(-1);
			stock.setUpdated(true);
			mcs.add(stock);
		}
		return mcs;
	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		return importFromDeck(importDeck(content, "defaultImport from " + getName()));
	}


	@Override
	public MTGDeck importDeckFromFile(File f) throws IOException {
		return importDeck(FileTools.readFile(f),FilenameUtils.getBaseName(f.getName()));
	}

	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileTools.readFile(f));
	}



}
