package org.magic.services.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.RequestBuilder.METHOD;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class MTGHttpClient {

	private HttpClient httpclient;
	private HttpClientContext httpContext;
	private BasicCookieStore cookieStore;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private HttpResponse response;
	private HttpClientConnectionManager connectionManager ;


	public HttpClient getHttpclient() {
		return httpclient;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public HttpClientContext getHttpContext() {
		return httpContext;
	}

	public MTGHttpClient() {

		connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.closeExpiredConnections();


		httpclient = HttpClients.custom()
					 .setUserAgent(MTGConstants.USER_AGENT)
					 .setRedirectStrategy(LaxRedirectStrategy.INSTANCE)
					 .setConnectionManager(connectionManager)
					 .setDefaultRequestConfig(RequestConfig.custom()
							 							   .setCookieSpec(CookieSpecs.STANDARD)
							 							   .setConnectTimeout(MTGConstants.CONNECTION_TIMEOUT)
							 							   .setSocketTimeout(MTGConstants.CONNECTION_TIMEOUT)
							 							   .setConnectionRequestTimeout(MTGConstants.CONNECTION_TIMEOUT)
							 							   .build())
					 .build();


		httpContext = new HttpClientContext();
		cookieStore = new BasicCookieStore();
		httpContext.setCookieStore(cookieStore);
	}

	public String toString(HttpResponse response) throws IOException
	{
		var ret = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		return ret;
	}

	public HttpClientConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public HttpResponse execute(HttpRequestBase req) throws IOException
	{
		var info = new NetworkInfo();
		info.setRequest(req);
		info.setStart(Instant.now());
		try{
			logger.debug("execute {}",req);
			response = httpclient.execute(req,httpContext);
			info.setReponse(response);
		}
		catch(Exception e)
		{
			logger.error( "uri={}",req.getURI(),e);
			info.setReponse(DefaultHttpResponseFactory.INSTANCE.newHttpResponse(new StatusLine() {

				@Override
				public int getStatusCode() {
					return -1;
				}

				@Override
				public String getReasonPhrase() {
					return e.getLocalizedMessage();
				}

				@Override
				public ProtocolVersion getProtocolVersion() {
					return null;
				}
			}, httpContext));
		}
		info.setEnd(Instant.now());
		AbstractTechnicalServiceManager.inst().store(info);
		return response;
	}


	public HttpResponse execute(RequestBuilder builder) throws IOException
	{

		if(builder.getMethod()== METHOD.GET)
			return doGet(builder.getUrl(),builder.getHeaders(),builder.getContent());

		if(builder.getMethod()== METHOD.POST)
			return doPost(builder.getUrl(), builder.getContent(), builder.getHeaders());

		if(builder.getMethod()== METHOD.PUT)
			return doPut(builder.getUrl(), builder.getContent(), builder.getHeaders());


		throw new IOException("choose a method with METHOD.POST/GET/PUT");

	}

	public HttpResponse doPut(String url, Map<String, String> entities, Map<String, String> headers) throws IOException {
		return doPut(url,new UrlEncodedFormEntity(entities.entrySet().stream().map(e-> new BasicNameValuePair(e.getKey(), e.getValue())).toList()),headers);
	}

	public HttpResponse doPut(String string, HttpEntity entities, Map<String, String> headers) throws IOException {
		var putReq = new HttpPut(string);
		try {
			if(entities!=null)
				putReq.setEntity(entities);

			if(headers!=null)
				headers.entrySet().forEach(e->putReq.addHeader(e.getKey(), e.getValue()));

			return execute(putReq);
		} catch (UnsupportedEncodingException e1) {
			throw new IOException(e1);
		}
	}

	public HttpResponse doPost(String url, Map<String,String> entities, Map<String,String> headers) throws IOException
	{
		return doPost(url,new UrlEncodedFormEntity(entities.entrySet().stream().map(e-> new BasicNameValuePair(e.getKey(), e.getValue())).toList()),headers);
	}


	public HttpResponse doPost(String url, HttpEntity entities, Map<String,String> headers) throws IOException
	{
		var postReq = new HttpPost(url);
			try {
				if(entities!=null)
					postReq.setEntity(entities);

				if(headers!=null)
					headers.entrySet().forEach(e->postReq.addHeader(e.getKey(), e.getValue()));

				return execute(postReq);

			} catch (UnsupportedEncodingException e1) {
				throw new IOException(e1);
			}

	}

	public HttpResponse doGet(String url, Map<String,String> headers,Map<String,String> entities) throws IOException
	{
		var getReq = new HttpGet(url);

		if(entities!=null && !entities.isEmpty())
		{
			try {
				var builder = new URIBuilder(url);
				entities.entrySet().forEach(e->builder.addParameter(e.getKey(),e.getValue()));
				getReq = new HttpGet(builder.build());
			} catch (URISyntaxException e1) {
				throw new IOException(e1);
			}
		}


		if(headers!=null && !headers.isEmpty())
		{
			for(Entry<String, String> e : headers.entrySet())
				getReq.addHeader(e.getKey(), e.getValue());

		}


		return  execute(getReq);

	}

	public HttpResponse doGet(String url) throws IOException
	{
		return doGet(url,null,null);
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



	public Builder<String, String> buildMap() {
		return new ImmutableMap.Builder<>();
	}

	public RequestBuilder build()
	{
		return RequestBuilder.build();
	}


}





