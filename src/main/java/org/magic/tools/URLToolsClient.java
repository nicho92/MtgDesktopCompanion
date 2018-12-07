package org.magic.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class URLToolsClient {

	private HttpClient httpclient;
	private BasicHttpContext httpContext;
	private BasicCookieStore cookieStore;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	
	public URLToolsClient() {
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();
		httpContext = new BasicHttpContext();
		cookieStore = new BasicCookieStore();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}
	
	public Document doPost(String url, List<NameValuePair> entities, Map<String,String> headers) throws IOException
	{
			logger.trace("POST " + url);
		
			HttpPost login = new HttpPost(url);
			try {
				if(entities!=null)
					login.setEntity(new UrlEncodedFormEntity(entities));
				
				if(headers!=null)
					headers.entrySet().forEach(e->login.addHeader(e.getKey(), e.getValue()));
				
				
				HttpResponse resp  = httpclient.execute(login,httpContext);
				Document d = URLTools.toHtml(EntityUtils.toString(resp.getEntity()));
				EntityUtils.consume(resp.getEntity());
				return d;
			} catch (UnsupportedEncodingException e1) {
				throw new IOException(e1);
			}

	}
	public Document doGet(String url) throws IOException
	{
		return doGet(url,null);
	}
	
	
	public Document doGet(String url, Map<String,String> headers) throws IOException
	{
		logger.trace("GET " + url);
		HttpGet get = new HttpGet(url);
		if(headers!=null)
			headers.entrySet().forEach(e->get.addHeader(e.getKey(), e.getValue()));

		HttpResponse resp  = httpclient.execute(get,httpContext);
		Document d = URLTools.toHtml(EntityUtils.toString(resp.getEntity()));
		EntityUtils.consume(resp.getEntity());
		return d;
	}
	

}
