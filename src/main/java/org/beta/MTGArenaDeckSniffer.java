package org.beta;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.exports.impl.MTGArenaExport;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.tools.FileTools;
import org.magic.tools.MTGArenaTools;

import com.google.gson.JsonObject;

public class MTGArenaDeckSniffer extends AbstractDeckSniffer {


	private static final String ARENA_LOG_FILE = "ARENA_LOG_FILE";

	@Override
	public String[] listFilter() {
		return new String[0];
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		JsonObject json = MTGArenaTools.readDecks(FileTools.readFile(getFile(ARENA_LOG_FILE)));
		
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		new MTGArenaDeckSniffer().getDeckList().forEach(rd->{
			
			System.out.println(rd.getName() +" " + rd.getUrl() + " " + rd.getDescription());
		});
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		
		List<RetrievableDeck> ret = new ArrayList<>();
		JsonObject json = MTGArenaTools.readDecks(FileTools.readFile(getFile(ARENA_LOG_FILE)));
		
		json.get("payload").getAsJsonArray().forEach(je->{
			
			JsonObject obj = je.getAsJsonObject();
			RetrievableDeck d = new RetrievableDeck();
							d.setName(obj.get("name").getAsString());
							
							try {
							d.setDescription(obj.get("description").getAsString());
							}
							catch(Exception e)
							{
								//do nothing
							}
							
							try {
								d.setUrl(new URI("file://"+obj.get("id").getAsString()));
							} catch (URISyntaxException e) {
								logger.error(e);
							}
							
							ret.add(d);
		});
		
		return ret;
	}

	@Override
	public String getName() {
		return "MTGArena";
	}
	
	@Override
	public void initDefault() {
		setProperty(ARENA_LOG_FILE,"C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\LocalLow\\Wizards Of The Coast\\MTGA\\Player.log");
	}
}
