package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.MTGStringUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

public class DeckTutorPricer extends AbstractMagicPricesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public static void main(String[] args) throws Exception {
		DeckTutorPricer pricer = new DeckTutorPricer();
		pricer.getPrice(null, null);
	}
	

	private BasicCookieStore cookieStore;

	private BasicHttpContext httpContext;
	
	ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	    public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
	      	 int status = response.getStatusLine().getStatusCode();
	         HttpEntity entity = response.getEntity();
	     	
	         if (status >= 200 && status < 300) 
	           {
	           	return entity != null ? EntityUtils.toString(entity) : null;
	           } 
	           else {
	           	  throw new ClientProtocolException("Unexpected response status: " + status + ":" + EntityUtils.toString(entity));
	           }
	        }
	    };
	    
	
	
	public DeckTutorPricer() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
		props.put("URL", "https://ws.decktutor.com/app/v1");
		props.put("WEBSITE", "https://www.decktutor.com");
		props.put("LANG", "en");
		props.put("LOGIN", "login");
		props.put("PASSWORD", "password");
		props.put("ENCODING", "UTF-8");
		save();
		}
		
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
	}

	
	private String getMD5(String chaine) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(chaine.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);
        return hashtext;
	}
	
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
			HttpClient httpClient = HttpClientBuilder.create().build();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			JsonObject jsonparams = new JsonObject();
			   jsonparams.addProperty("login", props.getProperty("LOGIN"));    
			   jsonparams.addProperty("password", props.getProperty("PASSWORD"));
	

			HttpPost reqCredential = new HttpPost(props.getProperty("URL")+"/account/login");
					 reqCredential.addHeader("content-type", "application/json");
	                 reqCredential.setEntity(new StringEntity(jsonparams.toString()));
	     			
	        String response = httpClient.execute(reqCredential, responseHandler,httpContext);
	       // MTGStringUtil.prettyPrint(response);
	        
	        JsonElement root = new JsonParser().parse(response);
	        
	        String auth_token=  root.getAsJsonObject().get("auth_token").getAsString();
	        String auth_token_secret = root.getAsJsonObject().get("auth_token_secret").getAsString();
	        int sequence=5;
	         
	        HttpPost reqSearch= new HttpPost(props.getProperty("URL")+"/search/serp");
			        reqSearch.addHeader("x-dt-Auth-Token", auth_token);
			        reqSearch.addHeader("x-dt-Sequence", String.valueOf(sequence));
			        reqSearch.addHeader("x-dt-Signature", getMD5(sequence+":"+auth_token_secret));
			        reqSearch.addHeader("Content-type", "application/json");
			        reqSearch.addHeader("Accept", "application/json");
			        reqSearch.addHeader("x-dt-cdb-Language","en");
			        reqSearch.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			        
			        jsonparams = new JsonObject();
		    		jsonparams.addProperty("name", "Emrakul, the Aeons Torn");
		    		jsonparams.addProperty("game", "mtg");
		    		jsonparams.addProperty("set", "ROE");
		    		
		    		JsonObject obj = new JsonObject();
			    			   obj.add("search", jsonparams);
			    			  
			    			  // obj.addProperty("limit","2");
			    	
			    	MTGStringUtil.prettyPrint(obj.toString());		   
			    	reqSearch.setEntity(new StringEntity(obj.toString()));   
			        response = httpClient.execute(reqSearch, responseHandler,httpContext);
			        MTGStringUtil.prettyPrint(response);
	        
	        sequence++;
	        
	        this.toString();
	        
		return null;
	}

	@Override
	public String getName() {
		return "Deck Tutor";
	}


	@Override
	public void alertDetected(List<MagicPrice> p) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
