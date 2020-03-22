package org.beta;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class CardTraderExport extends AbstractCardExport {

	private static final String TOKEN_FULL = "TOKEN_FULL";
	private static final String TOKEN_SIMPLE = "TOKEN_SIMPLE";
	private String baseUrl = "https://api.cardtrader.com/api"; 
	
	
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
	public String getVersion() {
		return "v1";
	}

	@Override
	public String getName() {
		return "CardTrader";
	}
	
	@Override
	public void initDefault() {
		setProperty(TOKEN_FULL, "");
		setProperty(TOKEN_SIMPLE, "");
	}

	public static void main(String[] args) throws IOException {
		new CardTraderExport().downloadProducts();
	}
	
	
	public void downloadCSV() throws IOException
	{
		String url = baseUrl+"/simple/"+getVersion()+"/products/download_csv?token="+getString(TOKEN_SIMPLE);
		File temp = new File(MTGConstants.DATA_DIR,"export.gz");
		URLTools.download(url,temp);
		logger.debug("Downloading " + url + " to " + temp.getAbsolutePath());
		FileTools.decompressGzipFile(temp, MTGConstants.DATA_DIR);
	}
	
	public File downloadProducts() throws IOException {
		String url = baseUrl+"/full/"+getVersion()+"/blueprints/export";
		File f = new File(MTGConstants.DATA_DIR,"test.json");
		FileTools.saveFile(f, RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(url)
							.addContent("category_id", "1")
							.addHeader("Authorization", "Bearer "+getString(TOKEN_FULL))
							.execute());
		
		return f;
		
	}
	
	
	
	
	
}
