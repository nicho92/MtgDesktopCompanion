package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGDeckManager;
import org.magic.tools.FileTools;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;

import com.google.gson.JsonElement;

import org.magic.tools.URLTools;

public class CardTraderExport extends AbstractCardExport {

	private static final String TOKEN = "TOKEN";
	private String baseUrl; 
	
	
	public CardTraderExport() {
		baseUrl= "https://api.cardtrader.com/api/simple/"+getVersion();
	}
	
	@Override
	public boolean needFile() {
		return false;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}
	
	@Override
	public String getFileExtension() {
		return null;
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);

	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not Implemented");
	}
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		
		RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET)
					  .url(baseUrl+"/download_csv")
					  .addContent("token", getString(TOKEN));
		
		File temp = new File(MTGConstants.DATA_DIR,"export.gz");
		URLTools.download(baseUrl+"/products/download_csv?token="+getString(TOKEN),temp);
		FileTools.decompressGzipFile(temp, MTGConstants.DATA_DIR);
	}
	
	@Override
	public String getVersion() {
		return "v1";
	}

	@Override
	public String getName() {
		return "CardTrader";
	}
	
	@Override
	public void initDefault() {
		setProperty(TOKEN, "");
	}

	public static void main(String[] args) throws IOException {
		new CardTraderExport().exportDeck(new MTGDeckManager().getDeck("Mengucci's Legacy Pox"), Paths.get("d:/", "temp").toFile());
	}
	
}
