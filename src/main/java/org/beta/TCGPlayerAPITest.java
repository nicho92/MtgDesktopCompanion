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
import org.magic.tools.URLToolsClient;

public class TCGPlayerAPITest extends AbstractMagicPricesProvider {

	public static void main(String[] args) throws IOException {
		
		new TCGPlayerAPITest().getLocalePrice(null,null);


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
	protected List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		URLToolsClient client = URLTools.newClient();
		RequestBuilder build = RequestBuilder.build()
							  .url("https://api.tcgplayer.com/token")
							  .method(METHOD.POST)
							  .addContent("grant_type","client_credentials")
							  .addContent("client_id",getString("CLIENT_ID"))
							  .addContent("client_secret",getString("CLIENT_SECRET"));
			String bearer = URLTools.toJson(client.execute(build)).getAsJsonObject().get("access_token").getAsString();


			
			build.url("http://api.tcgplayer.com/v1.19.0/pricing/product/11888").clearHeaders().clearContents()
				 .method(METHOD.GET)
				 .addHeader("Authorization", "bearer "+bearer);
		
		logger.debug(client.execute(build));
		
		
		return new ArrayList<>();
	}

}