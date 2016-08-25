package org.magic.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SearchTest {

	
	public static void main(String[] args) throws Exception {

		
		String tappedJson = "http://tappedout.net/api/deck/latest/modern/";
		CookieStore cookieStore = new BasicCookieStore();
		
		
		HttpClient httpclient = HttpClients.custom()
				 							.setUserAgent("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13")
				 							.setRedirectStrategy(new LaxRedirectStrategy())
				 							.build();
		 
		 HttpContext httpContext = new BasicHttpContext();
		 			 httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		 
		 ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

             @Override
             public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
           	 int status = response.getStatusLine().getStatusCode();
              HttpEntity entity = response.getEntity();
          	
              if (status >= 200 && status < 300) 
                {
                	return entity != null ? EntityUtils.toString(entity) : null;
                } 
                else {
                	  throw new ClientProtocolException("Unexpected response status: " + status);
                }
             }
         };
         
        httpclient.execute(new HttpGet("http://tappedout.net/accounts/login"), responseHandler,httpContext); //get CSRF
          
        HttpPost login = new HttpPost("http://tappedout.net/accounts/login/");
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        					 nvps.add(new BasicNameValuePair("next", "/"));
					         nvps.add(new BasicNameValuePair("username", "nicolas.pihen@gmail.com"));
					         nvps.add(new BasicNameValuePair("password", "XXXX"));
					         nvps.add(new BasicNameValuePair("csrfmiddlewaretoken", getCookieValue(cookieStore, "csrftoken")));
			login.setEntity(new UrlEncodedFormEntity(nvps));
			
		httpclient.execute(login, responseHandler,httpContext);

		
		String responseBody = httpclient.execute(new HttpGet(tappedJson), responseHandler,httpContext);
        
        
        JsonElement root = new JsonParser().parse(responseBody);
		
        
        String url_deck="";
		for(int i=0;i<root.getAsJsonArray().size();i++)
		{
			JsonObject obj = root.getAsJsonArray().get(i).getAsJsonObject();
			url_deck= obj.get("resource_uri").getAsString();
		}
		responseBody = httpclient.execute(new HttpGet(url_deck), responseHandler, httpContext);
		
		root = new JsonParser().parse(responseBody);
		
		System.out.println(root.getAsJsonObject().get("name").getAsString());
		System.out.println(root.getAsJsonObject().get("inventory"));
		for(int i=0;i<root.getAsJsonObject().get("inventory").getAsJsonArray().size();i++)
		{
			JsonArray a = root.getAsJsonObject().get("inventory").getAsJsonArray().get(i).getAsJsonArray();
			JsonObject b = a.get(1).getAsJsonObject();
			System.out.println(a.get(0) + " " + b.get("b").getAsString() + " " + b.get("qty").getAsInt());
		}
			
	}
	
	
	public static String getCookieValue(CookieStore cookieStore, String cookieName) 
	{
		String value = null;
		for (Cookie cookie: cookieStore.getCookies()) {
				if (cookie.getName().equals(cookieName)) {
						value = cookie.getValue();
						break;
				}
		}
		return value;
	}
	
}
