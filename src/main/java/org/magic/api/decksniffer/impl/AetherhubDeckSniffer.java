package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonElement;

public class AetherhubDeckSniffer extends AbstractDeckSniffer {

	private MTGHttpClient httpclient;
	private Map<String,String> formats;
	private String postReqData="{\"draw\":1,\"columns\":[{\"data\":\"name\",\"name\":\"name\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"color\",\"name\":\"color\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"tags\",\"name\":\"tags\",\"searchable\":true,\"orderable\":false,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"likes\",\"name\":\"likes\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"views\",\"name\":\"views\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"comments\",\"name\":\"comments\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"updated\",\"name\":\"updated\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"7\",\"regex\":false}},{\"data\":\"updatedhidden\",\"name\":\"updatedhidden\",\"searchable\":false,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}},{\"data\":\"popularity\",\"name\":\"popularity\",\"searchable\":false,\"orderable\":true,\"search\":{\"value\":\"\",\"regex\":false}}],\"order\":[{\"column\":8,\"dir\":\"desc\"}],\"start\":0,\"length\":50,\"search\":{\"value\":\"\",\"regex\":false}}";
	private String uriPost = "https://aetherhub.com/Meta/FetchMetaListAdv";

	public AetherhubDeckSniffer() {
		
		formats = new HashMap<>();
		formats.put("All", "");
		formats.put("Standard", "?formatId=1");
		formats.put("Modern", "?formatId=2");
		formats.put("Commander", "?formatId=3");
		formats.put("Legacy", "?formatId=4");
		formats.put("Vintage", "?formatId=5");

		formats.put("Commander 1vs1", "?formatId=7");
		formats.put("Frontier", "?formatId=8");
		formats.put("Pauper", "?formatId=9");
		formats.put("Brawl", "?formatId=10");
		formats.put("Limited", "?formatId=11");

		formats.put("MTGA Events Decks", "?formatId=13");
		formats.put("MTG Arena Standard", "?formatId=14");
		formats.put("Other", "?formatId=15");
		formats.put("MTGA Historic", "?formatId=16");
		formats.put("Pioneer", "?formatId=17");

		httpclient = URLTools.newClient();

	}
	

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}


	@Override
	public String[] listFilter() {
		return formats.keySet().toArray(new String[formats.keySet().size()]);
	}

	boolean sideboard=false;
	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {

		String uri="https://aetherhub.com/Deck/FetchDeckExport?deckId="+info.getUrl().getQuery().replace("id=","");
		var data = URLTools.extractAsJson(uri).getAsString();

		MTGDeck deck = info.toBaseDeck();
		sideboard=false;


		data.lines().forEach(s->{
			String line=s.trim();

			if(line.startsWith("Sideboard") || line.startsWith("Maybeboard"))
			{
				sideboard=true;
			}
			else if(!StringUtils.isBlank(line) && !line.equals("Deck"))
			{

					var entry = parseString(line);
					try
					{
						var mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(entry.getKey(), null, true).get(0);
						notify(mc);
						if(sideboard)
							deck.getSideBoard().put(mc, entry.getValue());
						else
							deck.getMain().put(mc, entry.getValue());
					}
					catch(Exception _)
					{
						logger.error("couldn't not find {}",entry.getKey());
					}
			}
		});
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {
		var list = new ArrayList<RetrievableDeck>();

		var headers = new HashMap<String,String>();
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		headers.put(URLTools.USER_AGENT,MTGConstants.USER_AGENT);
		var resp = httpclient.doPost(uriPost+formats.get(filter), new StringEntity(postReqData), headers);
		var ret = httpclient.toString(resp);

		logger.trace(ret);
		var el = URLTools.toJson(ret).getAsJsonObject();
		var arr = el.get("metadecks").getAsJsonArray();

		for(JsonElement je : arr)
		{
			var d = new RetrievableDeck();
			    d.setAuthor(je.getAsJsonObject().get("username").getAsString());
			    d.setName(je.getAsJsonObject().get("name").getAsString());
			    d.setDescription(UITools.formatDate(Instant.ofEpochMilli(je.getAsJsonObject().get("updated").getAsLong()/1000)));

			    var colors = je.getAsJsonObject().get("color").getAsJsonArray();
			    var temp = new StringBuilder();
			    if (colors.get(0).getAsInt() > 0) {
                   temp.append("{W}");
                }
                if (colors.get(1).getAsInt() > 0) {
                	temp.append("{U}");
                }
                if (colors.get(2).getAsInt() > 0) {
                	temp.append("{B}");
                }
                if (colors.get(3).getAsInt() > 0) {
                	temp.append("{R}");
                }
                if (colors.get(4).getAsInt() > 0) {
                	temp.append("{G}");
                }
                d.setColor(temp.toString());

                try {
					d.setUrl(new URI("https://aetherhub.com/Deck/Public?id="+je.getAsJsonObject().get("id").getAsInt()));
				} catch (URISyntaxException e) {
					logger.error(e);
				}

			    list.add(d);
		}
		return list;
	}


	@Override
	public String getName() {
		return "Aetherhub";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public String getVersion() {
		return "0.3";
	}


}
