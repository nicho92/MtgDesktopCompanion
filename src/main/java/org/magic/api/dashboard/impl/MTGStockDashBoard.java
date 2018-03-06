package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGConstants;

public class MTGStockDashBoard extends AbstractDashBoard {
	private String url = "http://www.mtgstocks.com/";
	private boolean connected;
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public MTGStockDashBoard() {
		super();
		connected=false;
	}
	
	private void connect() throws IOException
	{
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		HttpClient httpclient = HttpClients.custom()
					.setUserAgent(getString("USER_AGENT"))
					.setRedirectStrategy(new LaxRedirectStrategy())
					.build();
		
		HttpPost login = new HttpPost(url+"/users/sign_in");
	    List <NameValuePair> nvps = new ArrayList <>();
					         nvps.add(new BasicNameValuePair("user_username", getString("LOGIN")));
					         nvps.add(new BasicNameValuePair("user_password", getString("PASS")));
				 login.setEntity(new UrlEncodedFormEntity(nvps));
				 httpclient.execute(login, httpContext);
	
		connected=true;
	}
	
	
	
	@Override
	public List<CardShake> getShakerFor(String gameFormat) throws IOException {
		if(!connected)
			connect();
		
		return new ArrayList<>();
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {
		if(!connected)
			connect();
		
		return new ArrayList<>();
	}

	@Override
	public Map<Date, Double> getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {
		if(!connected)
			connect();
		
		return new HashMap<>();
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
		if(!connected)
			connect();
		
		return new ArrayList<>();
	}

	@Override
	public String[] getDominanceFilters() {
		return new String[] { "Legacy","Vintage", "Standard","Modern"};
	}


	@Override
	public void initDefault() {
		setProperty("LOGIN", "login@mail.com");
		setProperty("PASS", "changeme");
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		
	}

}
