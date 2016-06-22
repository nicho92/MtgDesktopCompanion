package org.magic.api.pricers.impl;

import java.io.File;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

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
		StringBuffer hexString = new StringBuffer();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest();

		for (int i = 0; i < hash.length; i++) {
		    if ((0xff & hash[i]) < 0x10) {
		        hexString.append("0"
		                + Integer.toHexString((0xFF & hash[i])));
		    } else {
		        hexString.append(Integer.toHexString(0xFF & hash[i]));
		    }
		}
		
		return hexString.toString();
	}
	
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(props.getProperty("URL")+"/account/login");
			request.addHeader("content-type", "application/json");
	        
		 	StringEntity params =new StringEntity("{\"login\":\""+props.getProperty("LOGIN")+"\",\"password\":\""+props.getProperty("PASSWORD")+"\"} ");
		 	
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
	        
	        List<NameValuePair> paramsearch = new LinkedList<NameValuePair>();
	        paramsearch.add(new BasicNameValuePair("game","mtg"));
	        paramsearch.add(new BasicNameValuePair("query","Jace, the mind"));
	      
	        HttpGet get = new HttpGet(props.getProperty("URL")+"/search/card/name?"+URLEncodedUtils.format(paramsearch, "utf-8"));
//			        get.addHeader("x-dt-Auth-Token: ", auth_token);
//			        get.addHeader("x-dt-Sequence: ", String.valueOf(sequence));
//			        get.addHeader("x-dt-Signature: ", signature);
//			        get.addHeader("content-type", "application/json");
//			        get.addHeader("Accept", "application/json");
			        get.addHeader("x-dt-language","french");
			        
			        
	        response = httpClient.execute(get);
	      
	        Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
	        System.out.println(s.next());
	        
	        
	        
	        this.toString();
	        
		return null;
	}

	@Override
	public String getName() {
		return "Deck Tutor";
	}
	
	
	
}
