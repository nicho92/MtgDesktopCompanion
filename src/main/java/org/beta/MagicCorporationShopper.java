package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.OrderEntry;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.MTGConstants;

public class MagicCorporationShopper extends AbstractMagicShopper {

	private String urlLogin ="https://boutique.magiccorporation.com/moncompte.php?op=login_db";
	private String urlCommandes="https://boutique.magiccorporation.com/moncompte.php?op=suivi_commande";
	private String urlDetailCommandes="https://boutique.magiccorporation.com/moncompte.php?op=commande&num_commande=";
	private HttpClient httpclient;
	private BasicHttpContext httpContext;
	private BasicCookieStore cookieStore;
	
	public MagicCorporationShopper() {
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();
		httpContext = new BasicHttpContext();
		cookieStore = new BasicCookieStore();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		
	}
	
	
	public static void main(String[] args) throws IOException {
		MagicCorporationShopper shop = new MagicCorporationShopper();
		
		shop.listOrders();
	}
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		List<OrderEntry> entries = new ArrayList<>();
		
		HttpPost login = new HttpPost(urlLogin);
		
		List<NameValuePair> nvps = new ArrayList<>();
				nvps.add(new BasicNameValuePair("email", getString("LOGIN")));
				nvps.add(new BasicNameValuePair("pass", getString("PASS")));
		login.setEntity(new UrlEncodedFormEntity(nvps));
		login.addHeader("Referer", "https://boutique.magiccorporation.com/moncompte.php?op=login");
		login.addHeader("Upgrade-Insecure-Requests", "1");
		login.addHeader("Origin", "https://boutique.magiccorporation.com");
		login.addHeader("Content-Type","application/x-www-form-urlencoded");
		login.addHeader("Upgrade-Insecure-Requests","1");
		login.addHeader("Accept-Encoding","gzip, deflate, br");
		login.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		login.addHeader("Host","boutique.magiccorporation.com");
		HttpResponse resp  = httpclient.execute(login,httpContext);
		//EntityUtils.consume(resp.getEntity());
		
		
		HttpGet listCommandes = new HttpGet(urlCommandes);
		resp =	httpclient.execute(listCommandes,httpContext);
		
		System.out.println(EntityUtils.toString(resp.getEntity()));
		EntityUtils.consume(resp.getEntity());
	
		
		
		return entries;
		
	}

	@Override
	public String getName() {
		return "MagicCorporation";
	}

	
	@Override
	public void initDefault() {
		setProperty("LOGIN","");
		setProperty("PASS", "");
	}
}
