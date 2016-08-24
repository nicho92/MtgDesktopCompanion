package org.magic.api.pricers.impl;

import java.io.File;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MagicFactory;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class DeckTutorPricer extends AbstractMagicPricesProvider {

	static final Logger logger = LogManager.getLogger(DeckTutorPricer.class.getName());
	
	private int sequence=1;
	
	public DeckTutorPricer() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
		props.put("URL", "https://ws.decktutor.com/app/v2");
		props.put("WEBSITE", "http://www.decktutor.com");
		props.put("LANG", "en");
		props.put("LOGIN", "login");
		props.put("PASSWORD", "password");
		props.put("ENCODING", "UTF-8");
		props.put("KEYWORD", "");
		save();
		}
	}

	
	public static void main(String[] args) {
		DeckTutorPricer pric = new DeckTutorPricer();
		
		try {
			pric.getPrice(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getMD5(String chaine) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest();
		StringBuffer hexString = new StringBuffer();
	    for (int i=0;i<hash.length;i++) {
	    		String hex=Integer.toHexString(0xff & hash[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     		hexString.append(hex);
	    }
		return hexString.toString();
	}
	
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(props.getProperty("URL")+"/account/login");
			request.addHeader("content-type", "application/json");
	        
			JsonObject json = new JsonObject();
    		json.addProperty("login", props.getProperty("LOGIN"));    
    		json.addProperty("password", props.getProperty("PASSWORD"));
			
		 	StringEntity params =new StringEntity(json.toString());
		 	request.setEntity(params);
	        
	        HttpResponse response = httpClient.execute(request);
	        
	        
	        JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
	        reader.beginObject();
	        
	        reader.nextName();
	        String auth_token=  reader.nextString();
	        
	        reader.nextName();
	        String expir = reader.nextString();
	        
	        reader.nextName();
	        String auth_token_secret = reader.nextString();
	        
	        reader.close();
	        
	        String signature = getMD5(sequence+":"+auth_token_secret);
	        
	        request= new HttpPost(props.getProperty("URL")+"/search/serp");
			        request.addHeader("x-dt-Auth-Token: ", auth_token);
			        request.addHeader("x-dt-Sequence: ", String.valueOf(sequence));
			        request.addHeader("x-dt-Signature: ", signature);
			        request.addHeader("content-type", "application/json");
			        request.addHeader("Accept", "application/json");
			        request.addHeader("x-dt-cdb-Language","fr");
		
			        
			        json = new JsonObject();
			    		json.addProperty("name", "Vindicate");
			    		json.addProperty("game", "mtg");
			    		JsonObject obj = new JsonObject();
			    		obj.add("search", json);
			    		
			    	System.out.println(obj);
			    	params =new StringEntity(obj.toString());
			    	
			    	request.setEntity(params);   
			        
	        response = httpClient.execute(request);
	      
	        Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
	       while(s.hasNext())
	        System.out.println(s.next());
	        
	        
	        
	        this.toString();
	        
		return null;
	}

	@Override
	public String getName() {
		return "Deck Tutor";
	}
	
	
	
}
