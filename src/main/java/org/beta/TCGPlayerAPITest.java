package org.beta;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.network.RequestBuilder.METHOD;

public class TCGPlayerAPITest extends AbstractPricesProvider {

	public static void main(String[] args) throws IOException {
		var mc = new MagicCard();
		mc.setTcgPlayerId(179466);
		new TCGPlayerAPITest().getLocalePrice(mc);
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		
		return Map.of("CLIENT_ID", "",
					"CLIENT_SECRET", "");
	}
	
	
	@Override
	public String getName() {
		return "TCGPlayer";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		
		var build = RequestBuilder.build()
							  .setClient(URLTools.newClient())
							  .url("https://api.tcgplayer.com/token")
							  .method(METHOD.POST)
							  .addContent("grant_type","client_credentials")
							  .addContent("client_id",getString("CLIENT_ID"))
							  .addContent("client_secret",getString("CLIENT_SECRET"));
		var bearer = build.toJson().getAsJsonObject().get("access_token").getAsString();


			
		var ret = build.clean()
								   .url("http://api.tcgplayer.com/v1.20.0/pricing/product/"+card.getTcgPlayerId())
								   .method(METHOD.GET)
								   .addHeader("Authorization", "bearer "+bearer).toJson();
		
		List<MagicPrice> list = new ArrayList<>();
		logger.debug(ret);
		ret.getAsJsonObject().get("results").getAsJsonArray().forEach(el->{
			
			if(!el.getAsJsonObject().get("marketPrice").isJsonNull()) {
				var p = new MagicPrice();
					   p.setCountry("USA");
					   p.setCurrency("USD");
					   p.setSite(getName());
					   p.setFoil(el.getAsJsonObject().get("subTypeName").getAsString().equals("Foil"));
					   p.setValue(el.getAsJsonObject().get("marketPrice").getAsDouble());
					   list.add(p);
			}
		});
		
		logger.debug(list);
		return list;
	}

}