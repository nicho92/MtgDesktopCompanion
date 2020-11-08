package org.beta;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;

public class TCGPlayerAPITest extends AbstractMagicPricesProvider {

	public static void main(String[] args) throws IOException {
		MagicCard mc = new MagicCard();
		mc.setTcgPlayerId(179466);
		new TCGPlayerAPITest().getLocalePrice(mc);
	}

	@Override
	public void initDefault() {
		setProperty("CLIENT_ID", "");
		setProperty("CLIENT_SECRET", "");
	}
	
	
	@Override
	public String getName() {
		return "TCGPlaplayer 2";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		
			RequestBuilder build = RequestBuilder.build()
							  .setClient(URLTools.newClient())
							  .url("https://api.tcgplayer.com/token")
							  .method(METHOD.POST)
							  .addContent("grant_type","client_credentials")
							  .addContent("client_id",getString("CLIENT_ID"))
							  .addContent("client_secret",getString("CLIENT_SECRET"));
			String bearer = build.toJson().getAsJsonObject().get("access_token").getAsString();


			
			JsonElement ret = build.clean()
								   .url("http://api.tcgplayer.com/v1.20.0/pricing/product/"+card.getTcgPlayerId())
								   .method(METHOD.GET)
								   .addHeader("Authorization", "bearer "+bearer).toJson();
		
		List<MagicPrice> list = new ArrayList<>();
		logger.debug(ret);
		ret.getAsJsonObject().get("results").getAsJsonArray().forEach(el->{
			
			if(!el.getAsJsonObject().get("marketPrice").isJsonNull()) {
				MagicPrice p = new MagicPrice();
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