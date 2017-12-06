package org.magic.api.dashboard.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;

public class MTGStockDashBoard extends AbstractDashBoard {
	private String url = "http://www.mtgstocks.com/";
	private CookieStore cookieStore;
	private HttpClient httpclient;
	private HttpContext httpContext; 
	

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public MTGStockDashBoard() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
				props.put("LOGIN", "login@mail.com");
				props.put("PASSWORD", "changeme");
				props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
				save();
		}
		
		try {
			connect();
		} catch (Exception e) {
			logger.error(e);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		MTGStockDashBoard board = new MTGStockDashBoard();
		
		
		
		
	}
	

	private void connect() throws Exception
	{
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		httpclient = HttpClients.custom()
					.setUserAgent(props.getProperty("USER_AGENT"))
					.setRedirectStrategy(new LaxRedirectStrategy())
					.build();
		
		HttpPost login = new HttpPost(url+"/users/sign_in"); //cookies need have tapped=buxy7qwywslb2tslv85ewqikh52xtztl;
	    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
					         nvps.add(new BasicNameValuePair("user_username", props.getProperty("LOGIN")));
					         nvps.add(new BasicNameValuePair("user_password", props.getProperty("PASSWORD")));
				 login.setEntity(new UrlEncodedFormEntity(nvps));
				 httpclient.execute(login, httpContext);
	
		
	}
	
	
	
	@Override
	public List<CardShake> getShakerFor(String gameFormat) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Date, Double> getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "MTG Stocks";
	}

	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public List<CardDominance> getBestCards(FORMAT f,String filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getDominanceFilters() {
		// TODO Auto-generated method stub
		return null;
	}

}
