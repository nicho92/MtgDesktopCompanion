package org.beta;

import java.io.IOException;

import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class TCPGPlayerAPITest {

	public static void main(String[] args) throws IOException {
		URLToolsClient client = URLTools.newClient();
		
		RequestBuilder builder = client.build();
		
		builder.url("https://api.tcgplayer.com/token").method(METHOD.POST)
			   .addContent("grant_type", "client_credentials")
			   .addContent("client_id","8DECCD88-5B2D-454D-A346-5338FDEF3540")
			   .addContent("client_secret","E298EB05-C493-486A-AF65-6B580D72E05E");
		
		
		String json = client.execute(builder);
		String bearer = URLTools.toJson(json).getAsJsonObject().get("access_token").getAsString();
		String s = "[{name:'ProductName', values:'Black Lotus'}]}";
		
		builder.clearContents().clearHeaders().url("http://api.tcgplayer.com/v1.20.0/catalog/categories/1/search")
												.addHeader("Authorization", "bearer "+bearer)
												.addContent("filters", s)
					  							.addContent("json", "true");
		
		System.out.println(builder);
		
		String ret = client.execute(builder);
		
		System.out.println(ret);
		
		
			
	}

}
