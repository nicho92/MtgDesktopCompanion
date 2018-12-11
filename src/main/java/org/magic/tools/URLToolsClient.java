package org.magic.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class URLToolsClient {

	private HttpClient httpclient;
	private BasicHttpContext httpContext;
	private BasicCookieStore cookieStore;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private HttpResponse response;
	
	public HttpResponse getResponse() {
		return response;
	}
	
	public URLToolsClient() {
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();
		httpContext = new BasicHttpContext();
		cookieStore = new BasicCookieStore();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}
	
	public String doPost(String url, List<NameValuePair> entities, Map<String,String> headers) throws IOException
	{
			return doPost(url,new UrlEncodedFormEntity(entities),headers);
		
	}
	
	public Map<String,String> buildMap()
	{
		return new HashMap<>();
	}
	
	public String doGet(String url, Map<String,String> headers) throws IOException
	{
		logger.trace("GET " + url);
		HttpGet getReq = new HttpGet(url);
		init(headers,getReq);
		response  = httpclient.execute(getReq,httpContext);
		return extractAndClose(response);
	}
	
	private String extractAndClose(HttpResponse response) throws IOException
	{
		logger.debug("return" + response.getStatusLine().getStatusCode());
		String ret = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		return ret;
	}
	
	private void init(Map<String,String> headers,HttpRequestBase req)
	{
		if(headers!=null)
			headers.entrySet().forEach(e->req.addHeader(e.getKey(), e.getValue()));
	}
	
	
	public String doPost(String url, HttpEntity entities, Map<String,String> headers) throws IOException
	{
			logger.trace("POST " + url);
		
			HttpPost postReq = new HttpPost(url);
			try {
				if(entities!=null)
					postReq.setEntity(entities);
				
				init(headers,postReq);
				
				response  = httpclient.execute(postReq,httpContext);
				return extractAndClose(response);
			} catch (UnsupportedEncodingException e1) {
				throw new IOException(e1);
			}

	}
	
	public String doGet(URI url) throws IOException
	{
		return doGet(url.toString(),null);
	}
	
	
	public String doGet(String url) throws IOException
	{
		return doGet(url,null);
	}
	
	
	

	public String getCookieValue(String cookieName) {
		String value = null;
		for (Cookie cookie : cookieStore.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				value = cookie.getValue();
				break;
			}
		}
		return value;
		
	}

	public List<Cookie> getCookies() {
		return cookieStore.getCookies();
	}
	

}
