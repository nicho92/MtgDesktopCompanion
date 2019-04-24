package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class TCPGPlayerAPITest {

	public static void main(String[] args) throws IOException {
		URLToolsClient client = URLTools.newClient();
		
		Map<String,String> header = new HashMap<>();
		header.put("Authorization", "");
		
		List<NameValuePair> entities = new ArrayList<>();
			entities.add(new BasicNameValuePair("grant_type","client_credentials"));
			entities.add(new BasicNameValuePair("client_id",""));
			entities.add(new BasicNameValuePair("client_secret",""));
			String json = client.doPost("https://api.tcgplayer.com/token",entities,header);
			String bearer = URLTools.toJson(json).getAsJsonObject().get("access_token").getAsString();
			
			
		Map<String,String> values = new HashMap<>();
		values.put("Authorization", "bearer "+bearer);
			
		String ret = client.doGet("http://api.tcgplayer.com/v1.19.0/catalog/categories/1/groups", values);
		
		System.out.println(ret);
		
		
			
	}

}
