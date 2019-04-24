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

import com.google.common.collect.ImmutableMap;

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
			
		
		entities.clear();
		
		String s = "[{name:'ProductName', values:'Black Lotus'}]}";
		
		entities.add(new BasicNameValuePair("filters", s));
		entities.add(new BasicNameValuePair("json", "true"));
		
		
		String ret = client.doPost("http://api.tcgplayer.com/v1.20.0/catalog/categories/1/search",entities, ImmutableMap.of("Authorization","bearer "+bearer));
		
		System.out.println(ret);
		
		
			
	}

}
