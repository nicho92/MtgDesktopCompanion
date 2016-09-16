package org.magic.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dao.impl.MysqlDAO;
import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.magic.services.MagicFactory;

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
      while ((line = rd.readLine()) != null) {  
           sb.append(line);  
      }
      rd.close();
       
      String _lastContent = sb.toString();
        
      System.out.println(_lastContent);
     
		
	}
}
