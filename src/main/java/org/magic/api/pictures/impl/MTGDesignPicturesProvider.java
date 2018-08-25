package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

public class MTGDesignPicturesProvider extends AbstractPicturesProvider {

	public static void main(String[] args) throws IOException {
		new MTGDesignPicturesProvider().getPicture(null, null);
	}

	private BasicHttpContext httpContext;
	private BasicCookieStore cookieStore;
	private HttpClient httpclient;
	
	public MTGDesignPicturesProvider() throws IOException {
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}
	
	
	private void connect() throws IOException {
		String u = "https://mtg.design/login";
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();

		HttpEntity p = httpclient.execute(new HttpGet(u), httpContext).getEntity();
		String token = URLTools.toHtml(EntityUtils.toString(p)).select("input[name=_token]").first().attr("value");
		HttpPost login = new HttpPost(u);
		List<NameValuePair> nvps = new ArrayList<>();
							nvps.add(new BasicNameValuePair("email", getString("LOGIN")));
							nvps.add(new BasicNameValuePair("password", getString("PASS")));
							nvps.add(new BasicNameValuePair("remember", "on"));
							nvps.add(new BasicNameValuePair("_token", token));
		
		login.setEntity(new UrlEncodedFormEntity(nvps));
		login.addHeader("Referer", u);
		login.addHeader("Upgrade-Insecure-Requests", "1");
		login.addHeader("Origin", "https://mtg.design");
		
		HttpResponse resp = httpclient.execute(login, httpContext);
		
		logger.debug("Connection : " +  resp.getStatusLine().getReasonPhrase());
		
	}


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException {
		if(cookieStore.getCookies().isEmpty())
			connect();

		
		URIBuilder build = new URIBuilder();
		build.setScheme("https").setHost("mtg.design").setPath("render");
		
		build.addParameter("card-title", mc.getName());
		build.addParameter("mana-cost", mc.getCost());
		build.addParameter("card-number", "1");
		build.addParameter("card-total", "5");
		build.addParameter("card-set", "MTD");
		build.addParameter("language", "EN");
		build.addParameter("super-type", "Legendary");
		build.addParameter("type", "Artifact Creature");
		build.addParameter("text-size", "38");
		
		build.addParameter("artwork", "http://images6.fanpop.com/image/photos/35500000/Colossal-Titan-titans-shingeki-no-kyojin-35500475-677-219.jpg");
		build.addParameter("designer", "nicho");
		build.addParameter("card-border", "black");
		build.addParameter("land-overlay", "C");
		build.addParameter("watermark", "0");
		build.addParameter("card-layout", "regular");
		build.addParameter("set-symbol", "0");
		build.addParameter("centered", "true");
		build.addParameter("foil", "false");
		build.addParameter("lighten", "false");
		build.addParameter("edit", "false");
		
		
		build.addParameter("card-accent", "W"); //binome de cost
		build.addParameter("card-template", "Gld"); 
		
		if(mc.getColors().size()>1)
		{
		}
		if(mc.getColors().size()>2)
		{
			build.addParameter("card-template", "Gld");
		}
		
		if(!mc.getArtist().isEmpty())
			build.addParameter("artist", mc.getArtist());
		
		if(!mc.getRarity().isEmpty())
			build.addParameter("rarity", mc.getRarity().substring(0,1).toUpperCase());
		else
			build.addParameter("rarity", "C");
		
		if(!mc.getText().isEmpty())
			build.addParameter("rules-text", mc.getText());
		
		if(!mc.getFlavor().isEmpty())
			build.addParameter("flavor-text", mc.getFlavor());
	
		if(!mc.getPower().isEmpty())
			build.addParameter("power", mc.getPower());
		
		if(!mc.getToughness().isEmpty())
			build.addParameter("toughness", mc.getToughness());

		
		
		
		HttpEntity p;
		try {
			logger.debug("generate " + build.build());
			HttpGet get = new HttpGet(build.build());
			p = httpclient.execute(get, httpContext).getEntity();
			logger.debug("generate done");
			BufferedImage im = ImageIO.read(p.getContent());
			EntityUtils.consume(p);
			return im;
		} catch (Exception e) {
			logger.error("error generate",e);
			return null;
		}
		
		
		
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Mtg.design";
	}
	
	@Override
	public void initDefault() {
		setProperty("LOGIN", "");
		setProperty("PASS", "");
		
	}

}
