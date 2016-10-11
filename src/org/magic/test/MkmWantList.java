package org.magic.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.magic.api.pricers.impl.MagicCardMarketPricer;

public class MkmWantList {

	
	public static void main(String[] args) throws Exception {
	  String link = "https://www.mkmapi.eu/ws/v1.1/wantslist";
	  String authorizationProperty = new MagicCardMarketPricer().generateOAuthSignature(link);
	  HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
			            connection.addRequestProperty("Authorization", authorizationProperty) ;
			            connection.connect();
      
	  int _lastCode = connection.getResponseCode();
      BufferedReader rd = new BufferedReader(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream()));  
      StringBuffer sb = new StringBuffer(); 
      String line;
      System.out.println("res : " + _lastCode);
      while ((line = rd.readLine()) != null) {  
           sb.append(line);  
      }
      
      rd.close();
	}
}
